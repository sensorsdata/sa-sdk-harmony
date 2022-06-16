package com.sensorsdata.analytics.harmony.sdk.core.database.provider.utils;

import com.sensorsdata.analytics.harmony.sdk.core.database.core.SADataContract;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.SAProviderStorage;
import ohos.app.Context;
import ohos.utils.net.Uri;

public class SAUriHelper {
    public static Uri generateContentUri(final Context context, SADataContract.Type type) {
        if (type == SADataContract.Type.SQLITE) {
            return generateContentUri(context, SADataContract.SQLite.BASE_PATH);
        } else if(type == SADataContract.Type.PREFERENCES){
            return generateContentUri(context, SADataContract.Preferences.BASE_PATH);
        }
        return new Uri.Builder().build();
    }

    private static Uri generateContentUri(final Context context, final String basePath) {
        final String authority = getAuthority(context);
        final Uri authorityUri = Uri.parse("dataability:///" + authority);
        return Uri.appendEncodedPathToUri(authorityUri, basePath);
    }

    private static synchronized String getAuthority(final Context context) {
        return context.getBundleName() + ".SensorsDataProvider";
    }

    public static Uri convertUri(Uri uri) {
        if (uri.toString().contains("///")) {
            uri = Uri.parse(uri.toString().replaceFirst("///", "//"));
        }
        return uri;
    }
}
