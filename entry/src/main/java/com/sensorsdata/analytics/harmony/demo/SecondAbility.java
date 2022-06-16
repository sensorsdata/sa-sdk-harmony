package com.sensorsdata.analytics.harmony.demo;

import com.sensorsdata.analytics.harmony.demo.slice.SecondAbilitySlice;
import com.sensorsdata.analytics.harmony.demo.slice.WebAbilitySlice;
import com.sensorsdata.analytics.harmony.sdk.SensorsDataAPI;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class SecondAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(SecondAbilitySlice.class.getName());
        //SensorsDataAPI.sharedInstance().track("s_test");
    }
}
