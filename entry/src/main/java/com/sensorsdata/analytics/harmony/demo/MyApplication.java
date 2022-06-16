package com.sensorsdata.analytics.harmony.demo;

import com.sensorsdata.analytics.harmony.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SALog;
import com.sensorsdata.analytics.harmony.sdk.core.SAConfigOptions;
import ohos.aafwk.ability.AbilityPackage;

public class MyApplication extends AbilityPackage {
    private final static String SERVER_URL = "http://10.120.73.51:8106/sa?project=production";

    @Override
    public void onInitialize() {
        super.onInitialize();
        SensorsDataAPI.startWithConfigOptions(this, new SAConfigOptions("http://10.120.73.51:8106/sa?project=production"));
//        SensorsDataAPI.sharedInstance().addPropertyPlugin(new LowProperty());
//        SensorsDataAPI.sharedInstance().addPropertyPlugin(new HeightProperty());
//        SensorsDataAPI.sharedInstance().addPropertyPlugin(new SuperProperty());
//        SensorsDataAPI.sharedInstance().addPropertyPlugin(new MiddleProperty());
    }

    @Override
    public void onEnd() {
        super.onEnd();
        SALog.i("SA.S", "Application end");
    }
}
