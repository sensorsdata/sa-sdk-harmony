package com.sensorsdata.analytics.harmony.sdk.common.utils;

import com.sensorsdata.analytics.harmony.sdk.common.constant.NetworkType;
import ohos.app.Context;
import ohos.security.SystemPermission;
import ohos.telephony.RadioInfoManager;
import ohos.telephony.SignalInformation;
import ohos.telephony.TelephonyConstants;
import ohos.wifi.WifiDevice;
import ohos.wifi.WifiLinkedInfo;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SANetworkUtils {
    private static final String TAG = "SA.Network";
    private static String sMacAddress;

    /**
     * HTTP 状态码 307
     */
    private static final int HTTP_307 = 307;

    private static String networkType;
    private static boolean mNetworkAvailable;

    /**
     * 判断指定网络类型是否可以上传数据
     *
     * @param networkType        网络类型
     * @param flushNetworkPolicy 上传策略
     * @return true：可以上传，false：不可以上传
     */
    public static boolean isShouldFlush(String networkType, int flushNetworkPolicy) {
        return (toNetworkType(networkType) & flushNetworkPolicy) != 0;
    }

    private static int toNetworkType(String networkType) {
        if ("NULL".equals(networkType)) {
            return NetworkType.TYPE_ALL;
        } else if ("WIFI".equals(networkType)) {
            return NetworkType.TYPE_WIFI;
        } else if ("2G".equals(networkType)) {
            return NetworkType.TYPE_2G;
        } else if ("3G".equals(networkType)) {
            return NetworkType.TYPE_3G;
        } else if ("4G".equals(networkType)) {
            return NetworkType.TYPE_4G;
        } else if ("5G".equals(networkType)) {
            return NetworkType.TYPE_5G;
        }
        return NetworkType.TYPE_ALL;
    }


    /**
     * 获取网络类型
     *
     * @param context Context
     * @return 网络类型
     */
    public static String networkType(Context context) {
        try {
            if (!mNetworkAvailable) {
                return "NULL";
            }
            if (!SATextUtils.isEmpty(networkType)) {
                return networkType;
            }
            // 检测权限
            if (SAPermissionUtils.checkPermission(context, SystemPermission.GET_WIFI_INFO)) {
                WifiDevice wifiDevice = WifiDevice.getInstance(context);
                if (wifiDevice.isConnected()) {
                    networkType = "WIFI";
                    return networkType;
                }
            } else {
                SALog.i("SA.Devices", "is not get wifi info permission");
                networkType = "NULL";
            }
            if (SAPermissionUtils.checkPermission(context, SystemPermission.GET_NETWORK_INFO)) {
                RadioInfoManager radioInfoManager = RadioInfoManager.getInstance(context);
                int primarySlotId = radioInfoManager.getPrimarySlotId();
                if (primarySlotId >= 0) {
                    List<SignalInformation> list = radioInfoManager.getSignalInfoList(primarySlotId);
                    for (SignalInformation signalInformation : list) {
                        int type = signalInformation.getNetworkType();
                        switch (type) {
                            case TelephonyConstants.NETWORK_TYPE_GSM:
                                networkType = "2G";
                                break;
                            case TelephonyConstants.NETWORK_TYPE_CDMA:
                            case TelephonyConstants.NETWORK_TYPE_WCDMA:
                            case TelephonyConstants.NETWORK_TYPE_TDSCDMA:
                                networkType = "3G";
                                break;
                            case TelephonyConstants.NETWORK_TYPE_LTE:
                                networkType = "4G";
                                break;
                            case TelephonyConstants.NETWORK_TYPE_NR:
                                networkType = "5G";
                                break;
                            default:
                                networkType = "NULL";
                        }
                    }
                }
            } else {
                SALog.i("SA.Devices", "is not get network info permission");
                networkType = "NULL";
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
            networkType = "NULL";
        }
        return networkType;
    }

    public static void changeNetworkState(boolean isAvailable) {
        mNetworkAvailable = isAvailable;
        networkType = null;
    }

    public static boolean needRedirects(int responseCode) {
        return responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HTTP_307;
    }


    public static String getLocation(HttpURLConnection connection, String path) throws MalformedURLException {
        if (connection == null || SATextUtils.isEmpty(path)) {
            return null;
        }
        String location = connection.getHeaderField("Location");
        if (SATextUtils.isEmpty(location)) {
            location = connection.getHeaderField("location");
        }
        if (SATextUtils.isEmpty(location)) {
            return null;
        }
        if (!(location.startsWith("http://") || location
                .startsWith("https://"))) {
            //某些时候会省略host，只返回后面的path，所以需要补全url
            URL originUrl = new URL(path);
            location = originUrl.getProtocol() + "://"
                    + originUrl.getHost() + location;
        }
        return location;
    }


    public static String getMacAddress(Context context) {
        if (!SATextUtils.isEmpty(sMacAddress)) {
            return sMacAddress;
        }
        try {
            if (SAPermissionUtils.checkPermission(context, SystemPermission.GET_WIFI_INFO)) {
                WifiDevice wifiDevice = WifiDevice.getInstance(context);
                if (wifiDevice.isConnected()) {
                    WifiLinkedInfo linkedInfo = wifiDevice.getLinkedInfo().get();
                    if (linkedInfo != null) {
                        sMacAddress = linkedInfo.getMacAddress();
                    }
                }
            } else {
                SALog.i("SA.Devices", "is not get wifi info permission");
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
            return null;
        }
        return sMacAddress;
    }

    /**
     * 是否有可用网络
     *
     * @param context Context
     * @return true：网络可用，false：网络不可用
     */
    public static boolean isNetworkAvailable(Context context) {
        // 检测权限
        if (!SAPermissionUtils.checkPermission(context, SystemPermission.GET_NETWORK_INFO)) {
            return false;
        }
        return mNetworkAvailable;
    }
}
