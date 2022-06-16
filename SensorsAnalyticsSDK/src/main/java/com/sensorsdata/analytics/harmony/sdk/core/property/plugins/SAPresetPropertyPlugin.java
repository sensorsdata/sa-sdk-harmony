package com.sensorsdata.analytics.harmony.sdk.core.property.plugins;

import com.sensorsdata.analytics.harmony.sdk.BuildConfig;
import com.sensorsdata.analytics.harmony.sdk.common.utils.*;
import com.sensorsdata.analytics.harmony.sdk.core.SAEventType;
import com.sensorsdata.analytics.harmony.sdk.core.property.SAPropertyPlugin;
import ohos.agp.utils.Point;
import ohos.app.Context;
import ohos.system.DeviceInfo;
import ohos.telephony.RadioInfoManager;
import ohos.telephony.SimInfoManager;
import ohos.telephony.TelephonyConstants;
import ohos.utils.zson.ZSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SAPresetPropertyPlugin extends SAPropertyPlugin {

    private final Context mContext;

    private String mCarrier;

    public SAPresetPropertyPlugin(Context context) {
        mContext = context;
    }

    /**
     * 重新读取运营商信息
     *
     * @return 运营商信息 String
     */
    private String getCarrier() {
        try {
            RadioInfoManager mRadioInfoManager = RadioInfoManager.getInstance(mContext);
            int primarySlotId = mRadioInfoManager.getPrimarySlotId();
            if (SimInfoManager.getInstance(mContext).getSimState(primarySlotId) == TelephonyConstants.SIM_STATE_LOADED) {
                return mRadioInfoManager.getOperatorName(primarySlotId);
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return "";
    }

    @Override
    public void appendDynamicProperties(ZSONObject properties) {
        if(SATextUtils.isEmpty(mCarrier)) {
            mCarrier = getCarrier();
            if(!SATextUtils.isEmpty(mCarrier)) {
                properties.put("$carrier", getCarrier());
            }
        }
    }

    @Override
    public void appendProperties(ZSONObject devicesProperties) {
        devicesProperties.put("$os", SAAppInfoUtils.OS_NAME);
        devicesProperties.put("$os_version", SAAppInfoUtils.getHarmonyOSVersion());

        devicesProperties.put("$manufacturer", SAAppInfoUtils.getManufacturer());
        devicesProperties.put("$brand", SAAppInfoUtils.getBrand());
        devicesProperties.put("$model", DeviceInfo.getModel());
        Point point = SADeviceUtils.getScreenSize(mContext);
        devicesProperties.put("$screen_width", point.getPointXToInt());
        devicesProperties.put("$screen_height", point.getPointYToInt());
        devicesProperties.put("$device_type", SADeviceUtils.getDeviceType(mContext));
        devicesProperties.put("$app_version", SAAppInfoUtils.getAppVersion(mContext));
        Integer zone_offset = SATimeUtils.getZoneOffset();
        if (zone_offset != null) {
            devicesProperties.put("$timezone_offset", zone_offset);
        }
        devicesProperties.put("$lib_version", BuildConfig.VERSION_NAME);
        devicesProperties.put("$app_id", SAAppInfoUtils.getProcessName(mContext));
        devicesProperties.put("$app_name", SAAppInfoUtils.getAppLabel(mContext));
        List<String> dataSources = new ArrayList<>();
        dataSources.add("HarmonyOS");
        devicesProperties.put("$data_ingestion_source", dataSources);
        mCarrier = getCarrier();
        if(!SATextUtils.isEmpty(mCarrier)) {
            devicesProperties.put("$carrier", getCarrier());
        }
    }

    @Override
    public void eventTypeFilter(Set<SAEventType> eventTypeFilter) {
        eventTypeFilter.add(SAEventType.TRACK);
        eventTypeFilter.add(SAEventType.TRACK_SIGNUP);
    }
}
