package com.sensorsdata.analytics.harmony.demo;

import com.sensorsdata.analytics.harmony.demo.slice.WebAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class WebAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(WebAbilitySlice.class.getName());
    }
}
