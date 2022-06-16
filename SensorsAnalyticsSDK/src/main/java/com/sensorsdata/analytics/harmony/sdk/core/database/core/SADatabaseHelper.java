package com.sensorsdata.analytics.harmony.sdk.core.database.core;

import com.sensorsdata.analytics.harmony.sdk.common.utils.SALog;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.data.rdb.RdbOpenCallback;
import ohos.data.rdb.RdbStore;
import ohos.data.rdb.StoreConfig;

public class SADatabaseHelper extends DatabaseHelper {
    private static final String TAG = "SA.DBHelper";

    private static final String CREATE_EVENTS_TABLE =
            String.format("create table if not exists %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s INTEGER NOT NULL);",
                    SADataContract.SQLite.Events.TABLE_NAME, SADataContract.SQLite.Events.Columns.PRIMARY_ID,
                    SADataContract.SQLite.Events.Columns.KEY_DATA, SADataContract.SQLite.Events.Columns.KEY_CREATED_AT);

    private final RdbStore mRdbStore;
    private final Preferences mPreferences;

    public SADatabaseHelper(Context context) {
        super(context);
        StoreConfig config = StoreConfig.newDefaultConfig(SADataContract.SQLite.DATABASE_NAME);
        mRdbStore = getRdbStore(config, SADataContract.SQLite.DATABASE_VERSION, callback);
        mPreferences = getPreferences(SADataContract.Preferences.NAME);
    }

    public RdbStore getRdbStore() {
        return mRdbStore;
    }

    public Preferences getPreferences() {
        return mPreferences;
    }


    private static final RdbOpenCallback callback = new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore rdbStore) {
            SALog.i(TAG, "SensorsDataAbility onCreate table:" + CREATE_EVENTS_TABLE);
            rdbStore.executeSql(CREATE_EVENTS_TABLE);
        }

        @Override
        public void onUpgrade(RdbStore rdbStore, int oldVersion, int newVersion) {
            SALog.i(TAG, "SensorsDataAbility onUpgrade table:" + CREATE_EVENTS_TABLE +
                    "oldVersion:" + oldVersion + "newVersion:" + newVersion);
        }
    };

}
