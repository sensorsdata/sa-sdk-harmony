package com.sensorsdata.analytics.harmony.sdk.channel;

import com.sensorsdata.analytics.harmony.sdk.common.utils.SATextUtils;

public class SAChannelUtils {
    public static String appendChannelSource(String dist, String key, String value) {
        if (SATextUtils.isEmpty(value)) {
            return dist;
        }
        if (SATextUtils.isEmpty(dist)) {
            return String.format("%s=%s", key, value);
        }
        return String.format("%s##%s=%s", dist, key, value);
    }
}
