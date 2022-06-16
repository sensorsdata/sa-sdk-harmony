package com.sensorsdata.analytics.harmony.sdk.core;

import com.sensorsdata.analytics.harmony.sdk.common.constant.ThreadNameConstants;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SALog;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SANetworkUtils;
import com.sensorsdata.analytics.harmony.sdk.core.database.SADataOperate;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.IPreferenceStorage;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.ISqliteStorage;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.SAProviderStorage;
import com.sensorsdata.analytics.harmony.sdk.core.property.SensorsDataPropertyPluginManager;
import com.sensorsdata.analytics.harmony.sdk.core.property.plugins.SAPresetPropertyPlugin;
import com.sensorsdata.analytics.harmony.sdk.core.property.plugins.SASuperPropertyPlugin;
import com.sensorsdata.analytics.harmony.sdk.core.property.plugins.SATrackPropertyPlugin;
import ohos.aafwk.ability.*;
import ohos.ace.ability.AceAbility;
import ohos.app.Context;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import ohos.net.NetStatusCallback;
import ohos.utils.PacMap;
import ohos.utils.zson.ZSONObject;

public class SensorsAnalyticsManager {
    private final static String TAG = "SA.SensorsAnalyticsManager";
    private static SensorsAnalyticsManager _INSTANCE;
    private final Context mContext;
    private final SAConfigOptions mConfigOptions;
    private final TrackTaskManager mTrackTaskManager;
    private final SAProviderStorage mProviderStorage;
    private AnalyticsMessages mAnalyticsMessage;
    boolean mNetworkRequest = true;
    private SAPresetPropertyPlugin mPresetPropertyPlugin;
    private SASuperPropertyPlugin mSuperPropertyPlugin;

    private final Object SUPER_PROPERTIES_LOCK = new Object();

    private SensorsAnalyticsManager(Context context, SAConfigOptions saConfigOptions) {
        this.mContext = context.getApplicationContext();
        this.mConfigOptions = saConfigOptions;
        mTrackTaskManager = TrackTaskManager.getInstance();
        startTrackThread();
        NetManager netManager = NetManager.getInstance(mContext);
        netManager.addDefaultNetStatusCallback(new NetStatusCallback() {
            @Override
            public void onAvailable(NetHandle handle) {
                super.onAvailable(handle);
                SANetworkUtils.changeNetworkState(true);
            }

            @Override
            public void onLost(NetHandle handle) {
                super.onLost(handle);
                SANetworkUtils.changeNetworkState(false);
            }
        });
        mProviderStorage = new SAProviderStorage(context);
        initOperate(mProviderStorage, mProviderStorage);
        registerSDKPropertyPlugin();
        registerJSConnectCallback(context);
    }

    private void registerJSConnectCallback(Context context) {
        if (context == null) {
            return;
        }
        SALifeCycle lifeCycle = new SALifeCycle();
        if (context instanceof AbilityPackage) {
            ((AbilityPackage) context).registerCallbacks(lifeCycle, null);
        } else if (context instanceof Ability) {
            ((Ability) context).getAbilityPackage().registerCallbacks(lifeCycle, null);
        } else if (context instanceof AbilitySlice) {
            ((AbilitySlice) context).getAbility().getAbilityPackage().registerCallbacks(lifeCycle, null);
        }
    }


    private class SALifeCycle implements AbilityLifecycleCallbacks {

        @Override
        public void onAbilityStart(Ability ability) {
            if (ability instanceof AceAbility) {
                SALog.i("SA.S", "register JS bridge");
                SALocalParticleAbility.getInstance().register((AceAbility) ability);
            }
        }

        @Override
        public void onAbilityActive(Ability ability) {

        }

        @Override
        public void onAbilityInactive(Ability ability) {

        }

        @Override
        public void onAbilityForeground(Ability ability) {
        }

        @Override
        public void onAbilityBackground(Ability ability) {

        }

        @Override
        public void onAbilityStop(Ability ability) {
            if (ability instanceof AceAbility) {
                SALocalParticleAbility.getInstance().deregister((AceAbility) ability);
            }
        }

        @Override
        public void onAbilitySaveState(PacMap pacMap) {

        }
    }

    public static void init(Context context, SAConfigOptions saConfigOptions) {
        if (_INSTANCE == null) {
            synchronized (SensorsAnalyticsManager.class) {
                if (_INSTANCE == null) {
                    _INSTANCE = new SensorsAnalyticsManager(context, saConfigOptions);
                }
            }
        }
    }

    public static SensorsAnalyticsManager getInstance() {
        return _INSTANCE;
    }

    public void startTrackThread() {
        try {
            new Thread(new TrackTaskManagerThread(), ThreadNameConstants.THREAD_TASK_QUEUE).start();
        } catch (Exception e) {
            SALog.e(TAG, "start event collect thread failed!");
        }
    }

    public SAConfigOptions getConfigOptions() {
        return mConfigOptions;
    }

    public void initOperate(IPreferenceStorage preferenceStorage, ISqliteStorage sqliteStorage) {
        SADataOperate.initOperate(mContext, preferenceStorage, sqliteStorage);
        mAnalyticsMessage = new AnalyticsMessages(mContext);
    }

    public void registerSDKPropertyPlugin() {
        mPresetPropertyPlugin = new SAPresetPropertyPlugin(mContext);
        mSuperPropertyPlugin = new SASuperPropertyPlugin();
        SensorsDataPropertyPluginManager.getInstance().registerPropertyPlugin(mPresetPropertyPlugin);
        SensorsDataPropertyPluginManager.getInstance().registerPropertyPlugin(mSuperPropertyPlugin);
        SensorsDataPropertyPluginManager.getInstance().registerPropertyPlugin(new SATrackPropertyPlugin(mContext));
    }

    public void addTrackEventTask(Runnable eventTask) {
        mTrackTaskManager.addTrackEventTask(eventTask);
    }

    public ZSONObject getPresetProperties() {
        if (mPresetPropertyPlugin != null) {
            return SensorsDataPropertyPluginManager.getInstance().getPropertiesByPlugin(mPresetPropertyPlugin.getClass());
        }
        return null;
    }

    public void enableNetworkRequest(boolean isEnable) {
        mNetworkRequest = isEnable;
    }

    public boolean isNetworkRequest() {
        return mNetworkRequest;
    }

    public void enqueueEventMessage(SAEventType type, ZSONObject eventJson) {
        mAnalyticsMessage.enqueueEventMessage(type, eventJson);
    }

    public void flush() {
        mTrackTaskManager.addTrackEventTask(mAnalyticsMessage::flush);
    }

    public void deleteAll() {
        mTrackTaskManager.addTrackEventTask(mAnalyticsMessage::deleteAll);
    }

    public long getMaxCacheSize() {
        if (mConfigOptions != null) {
            return mConfigOptions.mMaxCacheSize;
        }
        return 32 * 1024 * 1024L;
    }

    public String getServerUrl() {
        if (mConfigOptions != null) {
            return mConfigOptions.mServerUrl;
        }
        return "";
    }


    public ZSONObject getSuperProperties() {
        return SADataOperate.getInstance().getSuperProperties();
    }

    public void registerSuperProperties(ZSONObject superProperties) {
        try {
            if (superProperties == null) {
                return;
            }
            synchronized (SUPER_PROPERTIES_LOCK) {
                mSuperPropertyPlugin.registerProperties(superProperties);
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
    }

    public void unregisterSuperProperty(String superPropertyName) {
        try {
            synchronized (SUPER_PROPERTIES_LOCK) {
                mSuperPropertyPlugin.unregisterProperty(superPropertyName);
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
    }

    public void clearSuperProperties() {
        synchronized (SUPER_PROPERTIES_LOCK) {
            mSuperPropertyPlugin.clearProperties();
        }
    }


    public void registerPreferenceChangeListener(String key, IDataAbilityObserver observer) {
        if (mProviderStorage != null) {
            mProviderStorage.registerPreferenceChangeListener(key, observer);
        }
    }
}
