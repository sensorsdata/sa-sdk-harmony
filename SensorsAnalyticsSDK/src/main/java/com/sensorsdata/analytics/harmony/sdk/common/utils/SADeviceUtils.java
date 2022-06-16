package com.sensorsdata.analytics.harmony.sdk.common.utils;

import ohos.agp.utils.Point;
import ohos.agp.window.service.DisplayManager;
import ohos.app.AbilityContext;
import ohos.app.Context;
import ohos.global.configuration.DeviceCapability;
import ohos.global.resource.ResourceManager;
import ohos.security.SystemPermission;
import ohos.telephony.RadioInfoManager;
import ohos.telephony.SignalInformation;
import ohos.telephony.TelephonyConstants;
import ohos.wifi.WifiDevice;

import java.util.List;

public class SADeviceUtils {

    public static String getDeviceType(Context context) {
        ResourceManager resourceManager = context.getResourceManager();
        if(resourceManager == null){
            return null;
        }
        DeviceCapability deviceCapability = resourceManager.getDeviceCapability();
        if(deviceCapability == null){
            return null;
        }
        switch (deviceCapability.deviceType) {
            case DeviceCapability.DEVICE_TYPE_PHONE:
                return "PHONE";
            case DeviceCapability.DEVICE_TYPE_TABLET:
                return "TABLET";
            case DeviceCapability.DEVICE_TYPE_CAR:
                return "CAR";
            case DeviceCapability.DEVICE_TYPE_PC:
                return "PC";
            case DeviceCapability.DEVICE_TYPE_TV:
                return "TV";
            case DeviceCapability.DEVICE_TYPE_WEARABLE:
                return "WEARABLE";
            default:
                return null;
        }
    }

    /**
     * 获取手机屏幕尺寸
     *
     * @param context {@link Context}
     * @return {@link Point}
     */
    public static Point getScreenSize(Context context) {
        Point point = new Point();
        if (context != null) {
            DisplayManager.getInstance().getDefaultDisplay(context).get().getRealSize(point);
        }
        return point;
    }
}
