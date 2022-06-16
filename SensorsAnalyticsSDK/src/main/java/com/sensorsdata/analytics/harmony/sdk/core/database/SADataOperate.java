package com.sensorsdata.analytics.harmony.sdk.core.database;

import com.sensorsdata.analytics.harmony.sdk.common.utils.SATextUtils;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.IPreferenceStorage;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.ISqliteStorage;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.SADataContract;
import ohos.app.Context;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.utils.zson.ZSONObject;

import java.util.UUID;

public class SADataOperate {
    private static SADataOperate instance;
    private final Context mContext;
    private final IPreferenceStorage mPreferenceStorage;
    private final ISqliteStorage mSQLiteStorage;

    private SADataOperate(Context context, IPreferenceStorage preferenceStorage, ISqliteStorage sqliteStorage) {
        this.mContext = context;
        mPreferenceStorage = preferenceStorage;
        mSQLiteStorage = sqliteStorage;
    }

    public static void initOperate(Context context, IPreferenceStorage preferenceStorage, ISqliteStorage sqliteStorage) {
        if (instance == null) {
            instance = new SADataOperate(context, preferenceStorage, sqliteStorage);
        }
    }

    public static SADataOperate getInstance() {
        if (instance == null) {
            throw new IllegalStateException("The static method getInstance(Context context) should be called before calling getInstance()");
        }
        return instance;
    }

    public int insertEvent(ZSONObject eventData) {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString("data", eventData.toString());
        valuesBucket.putLong(SADataContract.SQLite.Events.Columns.KEY_CREATED_AT, System.currentTimeMillis());
        int ref = mSQLiteStorage.insert(SADataContract.SQLite.Events.TABLE_NAME, valuesBucket);
        if (ref != SADataContract.SQLite.DB_OUT_OF_MEMORY_ERROR) {
            return queryEventCount();
        }
        return ref;
    }

    public String[] queryEvent(int limit) {
        return mSQLiteStorage.query(SADataContract.SQLite.Events.TABLE_NAME, limit);
    }

    public int queryEventCount() {
        return mSQLiteStorage.queryRowCount(SADataContract.SQLite.Events.TABLE_NAME);
    }

    public void deleteAllEvent() {
        mSQLiteStorage.deleteAll(SADataContract.SQLite.Events.TABLE_NAME);
    }

    public int cleanupEvents(int lastId) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.between(SADataContract.SQLite.Events.Columns.PRIMARY_ID, 0, lastId);
        mSQLiteStorage.delete(SADataContract.SQLite.Events.TABLE_NAME, predicates);
        return mSQLiteStorage.queryRowCount(SADataContract.SQLite.Events.TABLE_NAME);
    }

    public void commitAnonymousId(String anonymousId) {
        mPreferenceStorage.putString(SADataContract.Preferences.ANONYMOUS_ID, anonymousId);
    }

    public String getAnonymousId() {
        String anonymousId = mPreferenceStorage.getString(SADataContract.Preferences.ANONYMOUS_ID, null);
        if (SATextUtils.isEmpty(anonymousId)) {
            anonymousId = UUID.randomUUID().toString();
            commitAnonymousId(anonymousId);
        }
        return anonymousId;
    }

    public void commitLoginId(String loginId) {
        mPreferenceStorage.putString(SADataContract.Preferences.LOGIN_ID, loginId);
    }

    public void remove(String key) {
        mPreferenceStorage.remove(key);
    }

    public String getLoginId() {
        return mPreferenceStorage.getString(SADataContract.Preferences.LOGIN_ID, null);
    }

    public String getString(String key, String defaultValue) {
        return mPreferenceStorage.getString(key, defaultValue);
    }

    public void putString(String key, String value) {
        mPreferenceStorage.putString(key, value);
    }

    public long getLong(String key, long defaultValue) {
        return mPreferenceStorage.getLong(key, defaultValue);
    }

    public void putLong(String key, long value) {
        mPreferenceStorage.putLong(key, value);
    }

    public boolean isFirstTrackInstallation(boolean disableCallback) {
        String key = disableCallback ? SADataContract.Preferences.FIRST_TRACK_INSTALLATION : SADataContract.Preferences.FIRST_TRACK_INSTALLATION_CALLBACK;
        return mPreferenceStorage.getBoolean(key, true);
    }

    public void commitFirstTrackInstallation(boolean disableCallback) {
        String key = disableCallback ? SADataContract.Preferences.FIRST_TRACK_INSTALLATION : SADataContract.Preferences.FIRST_TRACK_INSTALLATION_CALLBACK;
        mPreferenceStorage.putBoolean(key, false);
    }

    public ZSONObject getSuperProperties() {
        ZSONObject properties = mPreferenceStorage.getZSONObject(SADataContract.Preferences.SUPER_PROPERTIES);
        return properties;
    }

    public void commitSuperProperties(ZSONObject properties) {
        mPreferenceStorage.putZSONObject(SADataContract.Preferences.SUPER_PROPERTIES, properties);
    }
}
