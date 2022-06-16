package com.sensorsdata.analytics.harmony.demo.slice;

import com.sensorsdata.analytics.harmony.demo.JSAbility;
import com.sensorsdata.analytics.harmony.demo.ResourceTable;
import com.sensorsdata.analytics.harmony.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.harmony.sdk.common.constant.NetworkType;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SAZSONUtils;
import com.sensorsdata.analytics.harmony.sdk.common.utils.ToastUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.account.AccountAbility;
import ohos.utils.zson.ZSONObject;

public class SecondAbilitySlice extends AbilitySlice {

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_slice_second);

        findComponentById(ResourceTable.Id_login).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().login("ssl333");
        });
        findComponentById(ResourceTable.Id_logout).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().logout();
        });
        findComponentById(ResourceTable.Id_reset_anonymousid).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().resetAnonymousId();
        });
        findComponentById(ResourceTable.Id_identify).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().identify("sdd444");
//            SensorsDataAPI.sharedInstance().identify(null);
        });
        findComponentById(ResourceTable.Id_get_distinctid).setClickedListener(v -> {
            ToastUtils.show(this, SensorsDataAPI.sharedInstance().getDistinctId());
        });
        findComponentById(ResourceTable.Id_get_anonymousid).setClickedListener(v -> {
            ToastUtils.show(this, SensorsDataAPI.sharedInstance().getAnonymousId());
        });
        findComponentById(ResourceTable.Id_get_loginid).setClickedListener(v -> {
            ToastUtils.show(this, SensorsDataAPI.sharedInstance().getLoginId());
        });

        findComponentById(ResourceTable.Id_track).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().track("s_test");
        });

        findComponentById(ResourceTable.Id_timer_start).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().trackTimerStart("timer");
        });
        findComponentById(ResourceTable.Id_timer_pause).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().trackTimerPause("timer");
        });
        findComponentById(ResourceTable.Id_timer_resume).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().trackTimerResume("timer");
        });
        findComponentById(ResourceTable.Id_timer_end).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().trackTimerEnd("timer");
        });
        findComponentById(ResourceTable.Id_timer_clear).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().clearTrackTimer();
        });
        findComponentById(ResourceTable.Id_timer_remove).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().removeTimer("timer");
        });


        findComponentById(ResourceTable.Id_get_preset_properties).setClickedListener(v -> {
            ToastUtils.show(this, SAZSONUtils.formatJson(SensorsDataAPI.sharedInstance().getPresetProperties().toString()));
        });

    }
}
