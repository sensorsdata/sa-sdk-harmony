package com.sensorsdata.analytics.harmony.sdk.core.database.core;

import com.sensorsdata.analytics.harmony.sdk.common.utils.SALog;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SATextUtils;
import com.sensorsdata.analytics.harmony.sdk.core.database.provider.SensorsDataUri;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.IDataAbilityObserver;
import ohos.app.Context;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.utils.net.Uri;
import ohos.utils.zson.ZSONObject;

public class SAProviderStorage implements IPreferenceStorage, ISqliteStorage {

    private final DataAbilityHelper mHelper;
    private final Context mContext;

    public SAProviderStorage(Context context) {
        mContext = context.getApplicationContext();
        mHelper = DataAbilityHelper.creator(mContext);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.PREFERENCES).builder().setKey(key).build();
        boolean data = defaultValue;
        try {
            ResultSet resultSet = mHelper.query(uri, null, null);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                return data;
            }
            resultSet.goToLastRow();
            String result = resultSet.getString(resultSet.getColumnIndexForName(SADataContract.Preferences.COLUMN));
            if (result != null) {
                return Boolean.parseBoolean(result);
            }
        } catch (Exception e) {
            SALog.printStackTrace(e);
            data = defaultValue;
        }
        return data;
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.PREFERENCES).builder().setKey(key).build();
        try {
            ResultSet resultSet = mHelper.query(uri, null, null);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                return defaultValue;
            }
            resultSet.goToLastRow();
            return resultSet.getFloat(resultSet.getColumnIndexForName(SADataContract.Preferences.COLUMN));
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return defaultValue;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.PREFERENCES).builder().setKey(key).build();
        try {
            ResultSet resultSet = mHelper.query(uri, null, null);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                return defaultValue;
            }
            resultSet.goToLastRow();
            return resultSet.getInt(resultSet.getColumnIndexForName(SADataContract.Preferences.COLUMN));
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return defaultValue;
    }

    @Override
    public long getLong(String key, long defaultValue) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.PREFERENCES).builder().setKey(key).build();
        try {
            ResultSet resultSet = mHelper.query(uri, null, null);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                return defaultValue;
            }
            resultSet.goToLastRow();
            return resultSet.getLong(resultSet.getColumnIndexForName(SADataContract.Preferences.COLUMN));
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return defaultValue;
    }

    @Override
    public String getString(String key, String defaultValue) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.PREFERENCES).builder().setKey(key).build();
        try {
            ResultSet resultSet = mHelper.query(uri, null, null);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                return defaultValue;
            }
            resultSet.goToLastRow();
            return resultSet.getString(resultSet.getColumnIndexForName(SADataContract.Preferences.COLUMN));

        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return defaultValue;
    }

    @Override
    public void putBoolean(String key, boolean value) {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putBoolean(SADataContract.Preferences.COLUMN, value);
        put(key, valuesBucket);
    }

    private int put(String key, ValuesBucket valuesBucket) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.PREFERENCES).builder().setKey(key).build();
        int result = -1;
        try {
            result = mHelper.insert(uri, valuesBucket);
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return result;
    }

    @Override
    public void putFloat(String key, float value) {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putFloat(SADataContract.Preferences.COLUMN, value);
        put(key, valuesBucket);
    }

    @Override
    public void putInt(String key, int value) {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putInteger(SADataContract.Preferences.COLUMN, value);
        put(key, valuesBucket);
    }

    @Override
    public void putLong(String key, long value) {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putLong(SADataContract.Preferences.COLUMN, value);
        put(key, valuesBucket);
    }

    @Override
    public void putString(String key, String value) {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(SADataContract.Preferences.COLUMN, value);
        put(key, valuesBucket);
    }

    @Override
    public void putZSONObject(String key, ZSONObject value) {
        putString(key, value == null ? null : value.toString());
    }

    @Override
    public ZSONObject getZSONObject(String key) {
        try {
            String value = getString(key, null);
            return value == null ? new ZSONObject() : ZSONObject.stringToZSON(value);
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return null;
    }

    @Override
    public int remove(String key) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.PREFERENCES).builder().setKey(key).build();
        try {
            return mHelper.delete(uri, null);
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return -1;
    }

    @Override
    public int deleteAll(String tableName) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.SQLITE).builder().setKey(tableName).build();
        try {
            return mHelper.delete(uri, null);
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return -1;
    }

    @Override
    public int delete(String tableName, DataAbilityPredicates predicates) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.SQLITE).builder().setKey(tableName).build();
        try {
            return mHelper.delete(uri, predicates);
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return -1;
    }

    @Override
    public int insert(String tableName, ValuesBucket valuesBucket) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.SQLITE).builder().setKey(tableName).build();
        try {
            return mHelper.insert(uri, valuesBucket);
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return -1;
    }

    @Override
    public int bulkInsert(String tableName, Object data) {
        return 0;
    }

    @Override
    public String[] query(String tableName, int limit) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.SQLITE).builder().setKey(tableName).build();
        String data = null;
        String last_id = null;
        ResultSet resultSet = null;
        try {
            DataAbilityPredicates predicates = new DataAbilityPredicates();
            if (limit > 0) {
                predicates.limit(limit);
            }
            predicates.orderByAsc(SADataContract.SQLite.Events.Columns.KEY_CREATED_AT);
            resultSet = mHelper.query(uri, null, predicates);
            if (resultSet.getRowCount() != 0) {
                StringBuilder dataBuilder = new StringBuilder();
                final String flush_time = ",\"_flush_time\":";
                String suffix = ",";
                dataBuilder.append("[");
                String keyData;
                while (resultSet.goToNextRow()) {
                    if (resultSet.isAtLastRow()) {
                        suffix = "]";
                        last_id = resultSet.getString(resultSet.getColumnIndexForName(SADataContract.SQLite.Events.Columns.PRIMARY_ID));
                    }
                    keyData = resultSet.getString(resultSet.getColumnIndexForName(SADataContract.SQLite.Events.Columns.KEY_DATA));
                    keyData = parseData(keyData);
                    if (!SATextUtils.isEmpty(keyData)) {
                        dataBuilder.append(keyData, 0, keyData.length() - 1)
                                .append(flush_time)
                                .append(System.currentTimeMillis())
                                .append("}").append(suffix);
                    }
                }
                data = dataBuilder.toString();
            }
        } catch (Exception e) {
            last_id = null;
        } finally {
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
        }
        if (last_id != null) {
            return new String[]{last_id, data, SADataContract.SQLite.GZIP_DATA_EVENT};
        }
        return null;
    }

    @Override
    public String[] query(String tableName) {
        return query(tableName, 0);
    }

    @Override
    public int queryRowCount(String tableName) {
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.SQLITE).builder().setKey(tableName).build();
        ResultSet resultSet = null;
        try {
            DataAbilityPredicates predicates = new DataAbilityPredicates();
            resultSet = mHelper.query(uri, null, predicates);
            if (resultSet != null) {
                return resultSet.getRowCount();
            }
            return 0;
        } catch (Exception e) {
            SALog.printStackTrace(e);
        } finally {
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
        }
        return 0;
    }

    String parseData(String keyData) {
        try {
            if (SATextUtils.isEmpty(keyData)) return "";
            int index = keyData.lastIndexOf("\t");
            if (index > -1) {
                String crc = keyData.substring(index).replaceFirst("\t", "");
                keyData = keyData.substring(0, index);
                if (SATextUtils.isEmpty(keyData) || SATextUtils.isEmpty(crc)
                        || !crc.equals(String.valueOf(keyData.hashCode()))) {
                    return "";
                }
            }
        } catch (Exception ex) {
            SALog.printStackTrace(ex);
        }
        return keyData;
    }


    @Override
    public synchronized void registerPreferenceChangeListener(String key, IDataAbilityObserver observer) {
        if (observer == null || SATextUtils.isEmpty(key)) {
            return;
        }
        Uri uri = new SensorsDataUri(mContext, SADataContract.Type.PREFERENCES).builder().setKey(key).build();
        mHelper.registerObserver(uri, observer);
    }
}
