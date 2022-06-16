package com.sensorsdata.analytics.harmony.sdk.common.constant;

/**
 * 网络类型
 */
public class NetworkType {
    public static final int TYPE_NONE = 0;//NULL
    public static final int TYPE_2G = 1;//2G
    public static final int TYPE_3G = 1 << 1;//3G
    public static final int TYPE_4G = 1 << 2;//4G
    public static final int TYPE_WIFI = 1 << 3;//WIFI
    public static final int TYPE_5G = 1 << 4;//5G
    public static final int TYPE_ALL = 0xFF;//ALL
}