package com.sensorsdata.analytics.harmony.sdk.core.property.plugins;

import com.sensorsdata.analytics.harmony.sdk.common.utils.SANetworkUtils;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SATimeUtils;
import com.sensorsdata.analytics.harmony.sdk.core.database.SADataOperate;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.SADataContract;
import com.sensorsdata.analytics.harmony.sdk.core.property.SAPropertyPlugin;
import ohos.app.Context;
import ohos.miscservices.timeutility.Time;
import ohos.utils.zson.ZSONObject;

public class SATrackPropertyPlugin extends SAPropertyPlugin {
    private final Context mContext;
    private long mFirstDay = 0;

    public SATrackPropertyPlugin(Context context) {
        this.mContext = context;
    }

    @Override
    public void appendDynamicProperties(ZSONObject properties) {
        String networkType = SANetworkUtils.networkType(mContext);
        properties.put("$wifi", "WIFI".equals(networkType));
        properties.put("$network_type", networkType);
        properties.put("$is_first_day", isFirstDay());
    }

    /**
     * 是否首日
     *
     * @return 是否首日 ： boolean
     */
    private boolean isFirstDay() {
        if (mFirstDay == 0) {
            mFirstDay = SADataOperate.getInstance().getLong(SADataContract.Preferences.FIRST_DAY, 0);
            if (mFirstDay == 0) {
                //鸿蒙 Application 初始化时无法存储 Preferences ，第一次触发事件时存储首日时间戳
                mFirstDay = Time.getCurrentTime();
                SADataOperate.getInstance().putLong(SADataContract.Preferences.FIRST_DAY, mFirstDay);
                return true;
            }
        }
        return SATimeUtils.isSameDayOfMillis(mFirstDay, Time.getCurrentTime());
    }

    @Override
    public void appendProperties(ZSONObject properties) {

    }
}
