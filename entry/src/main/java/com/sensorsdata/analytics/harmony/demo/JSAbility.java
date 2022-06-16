package com.sensorsdata.analytics.harmony.demo;

import com.sensorsdata.analytics.harmony.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.harmony.sdk.core.SAConfigOptions;
import ohos.aafwk.content.Intent;
import ohos.ace.ability.AceAbility;

public class JSAbility extends AceAbility {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
//        super.setMainRoute(MainAbilitySlice.class.getName());
        SensorsDataAPI.startWithConfigOptions(this, new SAConfigOptions("http://10.120.73.51:8106/sa?project=production"));
    }

    @Override
    public void onActive() {
        super.onActive();
//        SensorsDataAPI.startWithConfigOptions(this, new SAConfigOptions("http://10.120.73.51:8106/sa?project=production"));
    }
}
