package com.sensorsdata.analytics.harmony.sdk.common.utils;

import ohos.app.AbilityContext;
import ohos.app.Context;
import ohos.bundle.IBundleManager;

import java.util.ArrayList;
import java.util.List;

public class SAPermissionUtils {
    private static final List<String> permissions = new ArrayList<>();

    public static boolean checkPermission(Context context, String permission) {
        if (permissions.contains(permission)) {
            return true;
        }
        if (context.verifySelfPermission(permission) == IBundleManager.PERMISSION_GRANTED) {
            permissions.add(permission);
            return true;
        }
        return false;
    }
}
