package com.sensorsdata.analytics.harmony.demo;

import com.sensorsdata.analytics.harmony.demo.slice.MainAbilitySlice;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SAAppInfoUtils;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SALog;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.LifecycleObserver;
import ohos.aafwk.content.Intent;
import ohos.bundle.IBundleManager;
import ohos.security.SystemPermission;

import java.lang.ref.WeakReference;

public class MainAbility extends Ability {

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        setMainRoute(MainAbilitySlice.class.getName());
//        SensorsDataAPI.startWithConfigOptions(this, new SAConfigOptions("http://10.120.73.51:8106/sa?project=production"));
        requestPermissionsFromUser(new String[]{SystemPermission.GET_TELEPHONY_STATE,SystemPermission.DISTRIBUTED_DATASYNC}, 1);

    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsFromUserResult(requestCode, permissions, grantResults);


        if (grantResults.length > 0 && grantResults[0] == IBundleManager.PERMISSION_GRANTED) {
            SALog.i("SA.Permission", "GRANTED");
        } else {
            SALog.i("SA.Permission", "DENIED");
        }
    }

    LifecycleObserver observer = new LifecycleObserver() {
        private WeakReference<Ability> sliceAbility;

        @Override
        public void onStart(Intent intent) {
            super.onStart(intent);
            SALog.i("SA.AutoTrack-Slice", "DialogAbilitySlice" + "onStart");
        }

        @Override
        public void onActive() {
            SALog.i("SA.AutoTrack-Slice", "DialogAbilitySlice" + "onActive");
        }

        @Override
        public void onInactive() {
            super.onInactive();
            SALog.i("SA.AutoTrack-Slice", "DialogAbilitySlice" + "onInactive");
        }

        @Override
        public void onBackground() {
            super.onBackground();

            SALog.i("SA.AutoTrack-Slice", "DialogAbilitySlice" + "onBackground");
        }

        @Override
        public void onForeground(Intent intent) {
            super.onForeground(intent);
            SALog.i("SA.AutoTrack-Slice", "DialogAbilitySlice" + "onForeground");
        }

        @Override
        public void onStop() {
            super.onStop();

            SALog.i("SA.AutoTrack-Slice", "DialogAbilitySlice" + "onStop");
        }
    };
}
