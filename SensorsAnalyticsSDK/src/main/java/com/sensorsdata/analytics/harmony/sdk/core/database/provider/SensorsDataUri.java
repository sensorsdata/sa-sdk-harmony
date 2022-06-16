package com.sensorsdata.analytics.harmony.sdk.core.database.provider;


import com.sensorsdata.analytics.harmony.sdk.core.database.core.SADataContract;
import com.sensorsdata.analytics.harmony.sdk.core.database.provider.utils.SAUriHelper;
import ohos.app.Context;
import ohos.utils.net.Uri;


/**
 * Internal helper class to build uris for the
 * <p>
 * Created by pascalwelsch on 6/11/15.
 */
public class SensorsDataUri {

    public final class Builder {

        private String mKey;

        public Builder(final Context context) {
            mContext = context.getApplicationContext();
        }

        public Builder setKey(final String key) {
            mKey = key;
            return this;
        }

        public Uri build() {
            final Uri.Builder builder = mContentUri.makeBuilder();
            if (mKey != null) {
                builder.appendDecodedPath(mKey);
            }
            return builder.build();
        }
    }

    private final Uri mContentUri;

    private Context mContext;

    public SensorsDataUri(Context context, SADataContract.Type type) {
        if(type == null){
            type = SADataContract.Type.PREFERENCES;
        }
        this.mContentUri = SAUriHelper.generateContentUri(context, type);
        mContext = context;
    }

    public Builder builder() {
        return new Builder(mContext);
    }

    public Uri get() {
        return mContentUri;
    }
}
