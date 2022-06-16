package com.sensorsdata.analytics.harmony.sdk;

import com.sensorsdata.analytics.harmony.sdk.channel.SAChannelUtils;
import com.sensorsdata.analytics.harmony.sdk.common.exception.InvalidDataException;
import com.sensorsdata.analytics.harmony.sdk.common.utils.*;
import com.sensorsdata.analytics.harmony.sdk.core.SAConfigOptions;
import com.sensorsdata.analytics.harmony.sdk.core.SAEventType;
import com.sensorsdata.analytics.harmony.sdk.core.SensorsAnalyticsManager;
import com.sensorsdata.analytics.harmony.sdk.core.bean.EventTimer;
import com.sensorsdata.analytics.harmony.sdk.core.database.SADataOperate;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.SADataContract;
import com.sensorsdata.analytics.harmony.sdk.core.property.SAPropertyPlugin;
import com.sensorsdata.analytics.harmony.sdk.core.property.SensorsDataPropertyPluginManager;
import ohos.app.AbilityContext;
import ohos.miscservices.timeutility.Time;
import ohos.utils.net.Uri;
import ohos.utils.zson.ZSONArray;
import ohos.utils.zson.ZSONObject;

import java.security.SecureRandom;
import java.util.*;

import static com.sensorsdata.analytics.harmony.sdk.common.utils.SADataHelper.*;


public class SensorsDataAPI implements ISensorsDataAPI {
    static final String TAG = "SA.SensorsDataAPI";
    private static SensorsDataAPI mInstance;
    private AbilityContext mContext;

    protected final Map<String, EventTimer> mTrackTimer = new HashMap<>();
    private String mLoginId;
    private final Object LOGIN_LOCK = new Object();
    private static SensorsDataEmptyAPI mEmptyAPI;

    private SensorsDataAPI(AbilityContext context, SAConfigOptions saConfigOptions) {
        try {
            mContext = context;
            SensorsAnalyticsManager.init(context, saConfigOptions.clone());
            SensorsAnalyticsManager.getInstance().registerPreferenceChangeListener(SADataContract.Preferences.LOGIN_ID, () -> {
                mLoginId = SADataOperate.getInstance().getLoginId();
                SALog.i(TAG, "loginId is change :" + (mLoginId == null ? "null" : mLoginId));
            });
        } catch (Exception e) {
            SALog.e(TAG, "SDK init failed,reason:" + e.getMessage());
        }
    }

    public static ISensorsDataAPI sharedInstance() {
        if (mInstance != null && SensorsAnalyticsManager.getInstance() != null) {
            return mInstance;
        } else {
            if (mEmptyAPI == null) {
                mEmptyAPI = new SensorsDataEmptyAPI();
            }
            return mEmptyAPI;
        }
    }

    public static void startWithConfigOptions(AbilityContext context, SAConfigOptions saConfigOptions) {
        synchronized (SensorsDataAPI.class) {
            if (mInstance == null) {
                mInstance = new SensorsDataAPI(context, saConfigOptions);
            }
        }
    }

    @Override
    public ZSONObject getPresetProperties() {
        return SensorsAnalyticsManager.getInstance().getPresetProperties();
    }

    @Override
    public void addPropertyPlugin(SAPropertyPlugin plugin) {
        SensorsDataPropertyPluginManager.getInstance().registerPropertyPlugin(plugin);
    }

    @Override
    public String getServerUrl() {
        return SensorsAnalyticsManager.getInstance().getServerUrl();
    }

    @Override
    public void setServerUrl(String serverUrl) {
        SensorsAnalyticsManager.getInstance().getConfigOptions().setServerUrl(serverUrl);
        if (SATextUtils.isEmpty(serverUrl)) {
            SALog.i(TAG, "Server url is null or empty.");
            return;
        }
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            Uri serverURI = Uri.parse(serverUrl);
            String hostServer = serverURI.getDecodedHost();
            if (!SATextUtils.isEmpty(hostServer) && hostServer.contains("_")) {
                SALog.i(TAG, "Server url " + serverUrl + " contains '_' is not recommend，" +
                        "see details: https://en.wikipedia.org/wiki/Hostname");
            }
        });
    }

    @Override
    public void enableNetworkRequest(boolean isRequest) {
        SensorsAnalyticsManager.getInstance().enableNetworkRequest(isRequest);
    }

    @Override
    public void setFlushNetworkPolicy(int networkType) {
        SensorsAnalyticsManager.getInstance().getConfigOptions().setNetworkTypePolicy(networkType);
    }

    @Override
    public String getDistinctId() {
        try {
            String loginId = getLoginId();
            if (!SATextUtils.isEmpty(loginId)) {
                return loginId;
            }
            return getAnonymousId();
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return "";
    }

    @Override
    public String getAnonymousId() {
        return SADataOperate.getInstance().getAnonymousId();
    }

    @Override
    public void resetAnonymousId() {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                SADataOperate.getInstance().commitAnonymousId(UUID.randomUUID().toString());
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });

    }

    @Override
    public String getLoginId() {
        // 防止客户 login 后立即调用 getLoginId 获取错误问题
        if (SATextUtils.isEmpty(mLoginId)) {
            return SADataOperate.getInstance().getLoginId();
        } else {
            return mLoginId;
        }
    }

    @Override
    public void identify(String anonymousId) {
        try {
            assertDistinctId(anonymousId);
        } catch (Exception e) {
            SALog.printStackTrace(e);
            return;
        }
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                if (!anonymousId.equals(SADataOperate.getInstance().getAnonymousId())) {
                    SADataOperate.getInstance().commitAnonymousId(anonymousId);
                }
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    @Override
    public void login(String loginId) {
        login(loginId, null);
    }

    @Override
    public void login(String loginId, ZSONObject properties) {
        try {
            assertDistinctId(loginId);
            synchronized (LOGIN_LOCK) {
                if (loginId.equals(getAnonymousId())) {
                    return;
                }
                mLoginId = loginId;
                SensorsAnalyticsManager.getInstance().addTrackEventTask(new Runnable() {
                    @Override
                    public void run() {
                        if (!loginId.equals(SADataOperate.getInstance().getLoginId())) {
                            SADataOperate.getInstance().commitLoginId(loginId);
                            trackEvent(SAEventType.TRACK_SIGNUP, "$SignUp", properties);
                        }
                    }
                });
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
    }

    @Override
    public void logout() {
        mLoginId = null;
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                synchronized (LOGIN_LOCK) {
                    SADataOperate.getInstance().commitLoginId(null);
                }
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    @Override
    public void trackAppInstall() {
        trackAppInstall(null, false);
    }

    @Override
    public void trackAppInstall(ZSONObject properties) {
        trackAppInstall(properties, false);
    }

    @Override
    public void trackAppInstall(ZSONObject properties, boolean disableCallback) {
        final ZSONObject eventProperties = new ZSONObject();
        if (properties != null) {
            SAZSONUtils.mergeZSONObject(properties, eventProperties);
        }
        if (!eventProperties.containsKey("$time")) {
            try {
                eventProperties.put("$time", new Date(System.currentTimeMillis()));
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        }
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                boolean isFirstTrackInstallation = SADataOperate.getInstance().isFirstTrackInstallation(disableCallback);
                if (isFirstTrackInstallation) {
                    String installSource = "";
                    if (eventProperties.containsKey("$oaid")) {
                        String oaid = eventProperties.getString("$oaid");
                        installSource = SAChannelUtils.appendChannelSource(installSource, "oaid", oaid);
                        eventProperties.remove("$oaid");
                    }
                    if (eventProperties.containsKey("$dvid")) {
                        installSource = SAChannelUtils.appendChannelSource(installSource, "dvid", eventProperties.getString("$dvid"));
                        eventProperties.remove("$dvid");
                    }
                    String macAddress = SANetworkUtils.getMacAddress(mContext);
                    if (!SATextUtils.isEmpty(macAddress)) {
                        installSource = SAChannelUtils.appendChannelSource(installSource, "mac", macAddress);
                    }
                    eventProperties.put("$ios_install_source", installSource);
                    if (disableCallback) {
                        eventProperties.put("$ios_install_disable_callback", disableCallback);
                    }
                    trackEvent(SAEventType.TRACK, "$AppInstall", eventProperties);
                    // 再发送 profile_set_once
                    ZSONObject profileProperties = new ZSONObject();
                    // 用户属性需要去掉 $ios_install_disable_callback 字段
                    eventProperties.remove("$ios_install_disable_callback");
                    SAZSONUtils.mergeZSONObject(eventProperties, profileProperties);
                    profileProperties.put("$first_visit_time", new java.util.Date());
                    trackEvent(SAEventType.PROFILE_SET_ONCE, null, profileProperties);
                    SADataOperate.getInstance().commitFirstTrackInstallation(disableCallback);
                    flush();
                }
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    @Override
    public void track(String eventName, ZSONObject properties) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                trackEvent(SAEventType.TRACK, eventName, properties);
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    @Override
    public void track(String eventName) {
        track(eventName, null);
    }

    @Override
    public void removeTimer(String eventName) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                assertEventName(eventName);
                synchronized (mTrackTimer) {
                    mTrackTimer.remove(eventName);
                }
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    @Override
    public void trackTimerEnd(String eventName, final ZSONObject properties) {
        long endTime = Time.getRealActiveTime();
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            ZSONObject _properties = new ZSONObject();
            if (eventName != null) {
                synchronized (mTrackTimer) {
                    EventTimer eventTimer = mTrackTimer.get(eventName);
                    if (eventTimer != null) {
                        eventTimer.setEndTime(endTime);
                        ZSONObject startProperties = eventTimer.getProperties();
                        if (startProperties != null && !startProperties.isEmpty()) {
                            SAZSONUtils.mergeZSONObject(startProperties, _properties);
                        }
                    }
                }
            }
            if (properties != null && !properties.isEmpty()) {
                SAZSONUtils.mergeZSONObject(properties, _properties);
            }
            try {
                trackEvent(SAEventType.TRACK, eventName, _properties);
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    @Override
    public void trackTimerEnd(String eventName) {
        trackTimerEnd(eventName, null);
    }

    @Override
    public void clearTrackTimer() {
        mTrackTimer.clear();
    }

    @Override
    public void flush() {
        SensorsAnalyticsManager.getInstance().flush();
    }

    @Override
    public ZSONObject getSuperProperties() {
        return SensorsAnalyticsManager.getInstance().getSuperProperties();
    }

    @Override
    public void registerSuperProperties(ZSONObject superProperties) {
        ZSONObject cloneProperties;
        try {
            cloneProperties = SAZSONUtils.cloneZsonObject(superProperties);
            SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
                try {
                    SensorsAnalyticsManager.getInstance().registerSuperProperties(cloneProperties);
                } catch (Exception e) {
                    SALog.printStackTrace(e);
                }
            });
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterSuperProperty(String superPropertyName) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                SensorsAnalyticsManager.getInstance().unregisterSuperProperty(superPropertyName);
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });

    }

    @Override
    public void clearSuperProperties() {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            SensorsAnalyticsManager.getInstance().clearSuperProperties();
        });
    }

    @Override
    public void profileSet(ZSONObject properties) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            trackEvent(SAEventType.PROFILE_SET, null, properties);
        });
    }

    @Override
    public void profileSet(String property, Object value) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            final ZSONObject _properties = new ZSONObject();
            _properties.put(property, value);
            trackEvent(SAEventType.PROFILE_SET, null, _properties);
        });
    }

    @Override
    public void profileSetOnce(ZSONObject properties) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            trackEvent(SAEventType.PROFILE_SET_ONCE, null, properties);
        });
    }

    @Override
    public void profileSetOnce(String property, Object value) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            final ZSONObject _properties = new ZSONObject();
            _properties.put(property, value);
            trackEvent(SAEventType.PROFILE_SET_ONCE, null, _properties);
        });
    }

    @Override
    public void profileIncrement(String property, Number value) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            final ZSONObject _properties = new ZSONObject();
            _properties.put(property, value);
            trackEvent(SAEventType.PROFILE_INCREMENT, null, _properties);
        });
    }

    @Override
    public void profileIncrement(Map<String, ? extends Number> properties) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            final ZSONObject _properties = new ZSONObject();
            _properties.putAll(properties);
            trackEvent(SAEventType.PROFILE_INCREMENT, null, _properties);
        });
    }

    @Override
    public void profileAppend(String property, String value) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                final ZSONArray append_values = new ZSONArray();
                append_values.add(value);
                final ZSONObject properties = new ZSONObject();
                properties.put(property, append_values);
                trackEvent(SAEventType.PROFILE_APPEND, null, properties);
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    @Override
    public void profileAppend(String property, Set<String> values) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(new Runnable() {
            @Override
            public void run() {
                try {
                    final ZSONArray append_values = new ZSONArray();
                    for (String value : values) {
                        append_values.add(value);
                    }
                    final ZSONObject _properties = new ZSONObject();
                    _properties.put(property, append_values);
                    trackEvent(SAEventType.PROFILE_APPEND, null, _properties);
                } catch (Exception e) {
                    SALog.printStackTrace(e);
                }
            }
        });
    }

    @Override
    public void profileUnset(String property) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                ZSONObject _properties = new ZSONObject();
                _properties.put(property, true);
                trackEvent(SAEventType.PROFILE_UNSET, null, _properties);
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    @Override
    public void profileDelete() {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                trackEvent(SAEventType.PROFILE_DELETE, null, null);
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    @Override
    public String trackTimerStart(String eventName) {
        return trackTimerStart(eventName, null);
    }

    @Override
    public String trackTimerStart(String eventName, ZSONObject properties) {
        try {
            final String eventNameRegex = String.format("%s_%s_%s", eventName, UUID.randomUUID().toString().replace("-", "_"), "SATimer");
            trackTimer(eventNameRegex, properties);
            trackTimer(eventName, properties);
            return eventNameRegex;
        } catch (Exception ex) {
            SALog.printStackTrace(ex);
        }
        return "";
    }

    @Override
    public void trackTimerPause(String eventName) {
        trackTimerState(eventName, true);
    }

    @Override
    public void trackTimerResume(String eventName) {
        trackTimerState(eventName, false);
    }

    @Override
    public void deleteAll() {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            SensorsAnalyticsManager.getInstance().deleteAll();
        });
    }

    @Override
    public void profilePushId(String pushTypeKey, String pushId) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                assertPropertyKey(pushTypeKey);
                if (SATextUtils.isEmpty(pushId)) {
                    SALog.i(TAG, "pushId is empty");
                    return;
                }
                String distinctId = getDistinctId();
                String distinctPushId = distinctId + pushId;
                String spDistinctPushId = SADataOperate.getInstance().getString("distinctId_" + pushTypeKey, "");
                if (!spDistinctPushId.equals(distinctPushId)) {
                    profileSet(pushTypeKey, pushId);
                    SADataOperate.getInstance().putString("distinctId_" + pushTypeKey, distinctPushId);
                }
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    @Override
    public void profileUnsetPushId(String pushTypeKey) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                assertPropertyKey(pushTypeKey);
                String distinctId = getDistinctId();
                String key = "distinctId_" + pushTypeKey;
                String spDistinctPushId = SADataOperate.getInstance().getString(key, "");

                if (spDistinctPushId.startsWith(distinctId)) {
                    profileUnset(pushTypeKey);
                    SADataOperate.getInstance().putString(key, null);
                }
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    @Override
    public void itemSet(String itemType, String itemId, ZSONObject properties) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            trackItemEvent(itemType, itemId, SAEventType.ITEM_SET, System.currentTimeMillis(), properties);
        });
    }

    @Override
    public void itemDelete(String itemType, String itemId) {
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            trackItemEvent(itemType, itemId, SAEventType.ITEM_DELETE, System.currentTimeMillis(), null);
        });
    }

    public void trackItemEvent(String itemType, String itemId, SAEventType eventType, long currentTimeMillis, ZSONObject properties) {
        try {
            boolean isItemTypeValid = SADataHelper.assertPropertyKey(itemType);
            assertItemId(itemId);
            assertPropertyTypes(properties);
            if (properties == null) {
                properties = new ZSONObject();
            }
            String eventProject = null;
            if (properties != null && properties.containsKey("$project")) {
                eventProject = (String) properties.get("$project");
                properties.remove("$project");
            }

            ZSONObject libProperties = new ZSONObject();
            libProperties.put("$lib", SAAppInfoUtils.LIB_NAME);
            libProperties.put("$lib_version", BuildConfig.VERSION_NAME);
            libProperties.put("$lib_method", "code");
            if (!properties.containsKey("$app_version")) {
                libProperties.put("$app_version", SAAppInfoUtils.getAppVersion(mContext));
            }
            StackTraceElement[] trace = (new Exception()).getStackTrace();
            if (trace.length > 1) {
                StackTraceElement traceElement = trace[0];
                String libDetail = String.format("%s##%s##%s##%s", traceElement
                                .getClassName(), traceElement.getMethodName(), traceElement.getFileName(),
                        traceElement.getLineNumber());
                if (!SATextUtils.isEmpty(libDetail)) {
                    libProperties.put("$lib_detail", libDetail);
                }
            }

            ZSONObject eventProperties = new ZSONObject();
            if (isItemTypeValid) {
                eventProperties.put("item_type", itemType);
            }
            eventProperties.put("item_id", itemId);
            eventProperties.put("type", eventType.getEventType());
            eventProperties.put("time", currentTimeMillis);
            eventProperties.put("properties", SATimeUtils.formatDate(properties));
            eventProperties.put("lib", libProperties);

            if (!SATextUtils.isEmpty(eventProject)) {
                eventProperties.put("project", eventProject);
            }

            SensorsAnalyticsManager.getInstance().enqueueEventMessage(eventType, eventProperties);
            if(SALog.isLogEnabled()) {
                SALog.i(TAG, "track event:\n" + SAZSONUtils.formatJson(eventProperties.toString()));
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
    }

    private void trackTimer(String eventName, ZSONObject properties) {
        final long startTime = Time.getRealActiveTime();
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                assertEventName(eventName);
                synchronized (mTrackTimer) {
                    mTrackTimer.put(eventName, new EventTimer(properties, startTime));
                }
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    /**
     * 触发事件的暂停/恢复
     *
     * @param eventName 事件名称
     * @param isPause   设置是否暂停
     */
    protected void trackTimerState(final String eventName, final boolean isPause) {
        final long startTime = Time.getRealActiveTime();
        SensorsAnalyticsManager.getInstance().addTrackEventTask(() -> {
            try {
                assertEventName(eventName);
                synchronized (mTrackTimer) {
                    EventTimer eventTimer = mTrackTimer.get(eventName);
                    if (eventTimer != null && eventTimer.isPaused() != isPause) {
                        eventTimer.setTimerState(isPause, startTime);
                    }
                }
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        });
    }

    void trackEvent(final SAEventType eventType, String eventName, final ZSONObject properties) {
        try {
            EventTimer eventTimer = null;
            if (!SATextUtils.isEmpty(eventName)) {
                synchronized (mTrackTimer) {
                    eventTimer = mTrackTimer.get(eventName);
                    mTrackTimer.remove(eventName);
                }

                if (eventName.endsWith("_SATimer") && eventName.length() > 45) {// Timer 计时交叉计算拼接的字符串长度 45
                    eventName = eventName.substring(0, eventName.length() - 45);
                }
            }
            if (eventType.isTrack()) {
                assertEventName(eventName);
            }
            assertPropertyTypes(properties);
            long eventTime = System.currentTimeMillis();
            try {
                ZSONObject dataObject = new ZSONObject();
                ZSONObject sendProperties = new ZSONObject();
                ZSONObject libProperties = new ZSONObject();
                // 合并属性插件中的属性
                SensorsDataPropertyPluginManager.getInstance().properties(eventName, eventType, sendProperties);
                // 合并自定义属性
                SAZSONUtils.mergeZSONObject(properties, sendProperties);
                try {
                    SecureRandom random = new SecureRandom();
                    dataObject.put("_track_id", random.nextInt());
                    dataObject.put("type", eventType.getEventType());
                    dataObject.put("time", eventTime);
                } catch (Exception e) {
                    // ignore
                }
                SAZSONUtils.mergeZSONObject(SAAppInfoUtils.getLibProperties(mContext), libProperties);
                try {
                    if (sendProperties.containsKey("$project")) {
                        dataObject.put("project", sendProperties.getString("$project"));
                        sendProperties.remove("$project");
                    }

                    if (sendProperties.containsKey("$token")) {
                        dataObject.put("token", sendProperties.getString("$token"));
                        sendProperties.remove("$token");
                    }

                    if (sendProperties.containsKey("$time")) {
                        try {
                            Object timeDate = sendProperties.get("$time");
                            if (timeDate instanceof Date) {
                                if (SATimeUtils.isDateValid((Date) timeDate)) {
                                    dataObject.put("time", ((Date) timeDate).getTime());
                                }
                            }
                        } catch (Exception ex) {
                            SALog.printStackTrace(ex);
                        }
                        sendProperties.remove("$time");
                    }
                } catch (Exception e) {
                    SALog.printStackTrace(e);
                }
                dataObject.put("lib", libProperties);
                dataObject.put("properties", sendProperties);
                if (eventType.isTrack() || eventType == SAEventType.TRACK_SIGNUP) {
                    dataObject.put("event", eventName);
                }
                String loginId = SADataOperate.getInstance().getLoginId();
                String anonymousId = SADataOperate.getInstance().getAnonymousId();
                if (!SATextUtils.isEmpty(loginId)) {
                    dataObject.put("login_id", loginId);
                    dataObject.put("distinct_id", loginId);
                } else {
                    dataObject.put("distinct_id", anonymousId);
                }
                if (eventType == SAEventType.TRACK_SIGNUP) {
                    dataObject.put("original_id", SensorsDataAPI.sharedInstance().getAnonymousId());
                }
                dataObject.put("anonymous_id", anonymousId);

                if (null != eventTimer) {
                    try {
                        double duration = Double.parseDouble(eventTimer.duration());
                        if (duration > 0) {
                            sendProperties.put("event_duration", duration);
                        }
                    } catch (Exception e) {
                        SALog.printStackTrace(e);
                    }
                }
                if(SALog.isLogEnabled()) {
                    SALog.i(TAG, SAZSONUtils.formatJson(dataObject.toString()));
                }
                SensorsAnalyticsManager.getInstance().enqueueEventMessage(eventType, dataObject);
            } catch (Exception e) {
                SALog.printStackTrace(e);
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
    }
}
