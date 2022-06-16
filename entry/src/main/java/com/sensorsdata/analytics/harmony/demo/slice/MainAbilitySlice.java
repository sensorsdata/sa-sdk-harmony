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

public class MainAbilitySlice extends AbilitySlice {
    int i = 0;
    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_slice_main);
        findComponentById(ResourceTable.Id_Id_goto_js).setClickedListener((component -> {
            Operation operation = new Intent.OperationBuilder()
                    .withBundleName(this.getBundleName())
                    .withAbilityName(JSAbility.class).build();
            Intent intent2 = new Intent();
            intent2.setOperation(operation);
            startAbility(intent2);
        }));
        findComponentById(ResourceTable.Id_set_server_url).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().setServerUrl("www.baidu.com");
        });
        findComponentById(ResourceTable.Id_get_server_url).setClickedListener(v -> {
            ToastUtils.show(this, SensorsDataAPI.sharedInstance().getServerUrl());
        });
        findComponentById(ResourceTable.Id_network_request).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().enableNetworkRequest(true);
        });
        findComponentById(ResourceTable.Id_flush_network_policy).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().setFlushNetworkPolicy(NetworkType.TYPE_2G|NetworkType.TYPE_3G);
        });
        findComponentById(ResourceTable.Id_login).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().login("mainProcess");
        });
        findComponentById(ResourceTable.Id_logout).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().logout();
        });
        findComponentById(ResourceTable.Id_reset_anonymousid).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().resetAnonymousId();
        });
        findComponentById(ResourceTable.Id_identify).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().identify("identify");
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
        findComponentById(ResourceTable.Id_appinstall).setClickedListener(v -> {
            ZSONObject object  = new ZSONObject();
            object.put("$oaid","oaid");
            object.put("$dvid", AccountAbility.getAccountAbility().getDistributedVirtualDeviceId());
            SensorsDataAPI.sharedInstance().trackAppInstall(object);
        });
        findComponentById(ResourceTable.Id_track).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().track("track");
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
        findComponentById(ResourceTable.Id_flush).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().flush();
        });
        findComponentById(ResourceTable.Id_delete_event).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().deleteAll();
        });

        findComponentById(ResourceTable.Id_get_preset_properties).setClickedListener(v -> {
            ToastUtils.show(this, SAZSONUtils.formatJson(SensorsDataAPI.sharedInstance().getPresetProperties().toString()));
        });
        findComponentById(ResourceTable.Id_profile_pushid).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().profilePushId("pushTypeKey","pushId");
        });
        findComponentById(ResourceTable.Id_profile_unset_pushid).setClickedListener(v -> {
            SensorsDataAPI.sharedInstance().profileUnsetPushId("pushTypeKey");
        });
    }
}
