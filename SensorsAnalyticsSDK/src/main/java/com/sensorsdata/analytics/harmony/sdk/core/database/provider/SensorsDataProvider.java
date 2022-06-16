package com.sensorsdata.analytics.harmony.sdk.core.database.provider;

import com.sensorsdata.analytics.harmony.sdk.common.utils.SALog;
import com.sensorsdata.analytics.harmony.sdk.core.SensorsAnalyticsManager;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.SADataContract;
import com.sensorsdata.analytics.harmony.sdk.core.database.core.SADatabaseHelper;
import com.sensorsdata.analytics.harmony.sdk.core.database.provider.utils.SAUriHelper;
import com.sensorsdata.analytics.harmony.sdk.core.database.provider.utils.UriMatcher;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.dataability.DataAbilityUtils;
import ohos.data.preferences.Preferences;
import ohos.data.rdb.RdbPredicates;
import ohos.data.rdb.RdbStore;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.data.resultset.TableResultSet;
import ohos.utils.LruBuffer;
import ohos.utils.PacMap;
import ohos.utils.net.Uri;

import java.io.File;
import java.io.FileDescriptor;

public class SensorsDataProvider extends Ability {
    private static final String TAG = "SA.DATAABILITY";
    private RdbStore mStore;
    private Preferences mPreference;
    private static final String LOCK_PREFIX = "SENSORS_PREFERENCE_STRING_LOCK_";
    private LruBuffer<String, String> lruBuffer = new LruBuffer<>();

    private static UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        SADatabaseHelper helper = new SADatabaseHelper(this);
        mStore = helper.getRdbStore();
        mPreference = helper.getPreferences();
        setAuthority(getBundleName() + ".SensorsDataProvider");
        SALog.i(TAG,"Provider create finish!");
    }

    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        uri = SAUriHelper.convertUri(uri);
        int matchCode = sURIMatcher.match(uri);
        String key = uri.getDecodedPathList().get(1);
        switch (matchCode) {
            case SADataContract.MatchCode.PREFERENCE_CODE:
                return queryPreferences(key);
            case SADataContract.MatchCode.SQLITE_CODE:
                RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, key);
                return mStore.query(rdbPredicates, columns);
        }
        return null;
    }

    private ResultSet queryPreferences(String key) {
        synchronized ((LOCK_PREFIX + key).intern()) {
            String value = null;
            if (lruBuffer.contains(key)) {
                value = lruBuffer.get(key);
            } else if (mPreference != null) {
                value = mPreference.getString(key, null);
                if (value != null) {
                    lruBuffer.put(key, value);
                }
            }
            if(value == null){
                return null;
            }
            TableResultSet resultSet = new TableResultSet(new String[]{SADataContract.Preferences.COLUMN});
            resultSet.addRow(new String[]{value});
            return resultSet;
        }
    }

    private int insertPreferences(Uri uri, String key, Object value) {
        if (key == null) {
            return -1;
        }
        synchronized ((LOCK_PREFIX + key).intern()) {
            if (mPreference != null) {
                if (value != null) {
                    mPreference.putString(key, String.valueOf(value));
                    mPreference.flushSync();
                    if (lruBuffer != null) {
                        lruBuffer.put(key, String.valueOf(value));
                    }
                } else {
                    mPreference.delete(key);
                    mPreference.flushSync();
                    if (lruBuffer != null) {
                        lruBuffer.remove(key);
                    }
                }
                DataAbilityHelper.creator(this).notifyChange(uri);
                return 1;
            }
        }
        return -1;
    }

    private int deletePreferences(String key) {
        synchronized ((LOCK_PREFIX + key).intern()) {
            if (mPreference != null) {
                mPreference.delete(key);
                mPreference.flushSync();

                lruBuffer.remove(key);
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int insert(Uri uri, ValuesBucket value) {
        Uri convertUri = SAUriHelper.convertUri(uri);
        int matchCode = sURIMatcher.match(convertUri);
        String key = convertUri.getDecodedPathList().get(1);
        switch (matchCode) {
            case SADataContract.MatchCode.PREFERENCE_CODE:
                return insertPreferences(uri, key, value.getObject(SADataContract.Preferences.COLUMN));
            case SADataContract.MatchCode.SQLITE_CODE:
                File file = new File(mStore.getPath());
                long fileLength = file.length();
                if (fileLength > SensorsAnalyticsManager.getInstance().getMaxCacheSize()) {
                    return SADataContract.SQLite.DB_OUT_OF_MEMORY_ERROR;
                }
                return mStore.insert(key, value) == -1 ? -1 : 1;
        }
        return -1;
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        uri = SAUriHelper.convertUri(uri);
        SALog.i(TAG, "SensorsDataAbility delete: " + uri.toString());
        int matchCode = sURIMatcher.match(uri);
        String key = uri.getDecodedPathList().get(1);
        switch (matchCode) {
            case SADataContract.MatchCode.PREFERENCE_CODE:
                return deletePreferences(key);
            case SADataContract.MatchCode.SQLITE_CODE:
                RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, key);
                return mStore.delete(rdbPredicates);
        }
        return -1;
    }

    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        return 0;
    }

    @Override
    public FileDescriptor openFile(Uri uri, String mode) {
        return null;
    }

    @Override
    public String[] getFileTypes(Uri uri, String mimeTypeFilter) {
        return new String[0];
    }

    @Override
    public PacMap call(String method, String arg, PacMap extras) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


    static void setAuthority(final String authority) {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(authority, SADataContract.SQLite.BASE_PATH + "/*", SADataContract.MatchCode.SQLITE_CODE);
        sURIMatcher.addURI(authority, SADataContract.Preferences.BASE_PATH + "/*", SADataContract.MatchCode.PREFERENCE_CODE);
    }
}