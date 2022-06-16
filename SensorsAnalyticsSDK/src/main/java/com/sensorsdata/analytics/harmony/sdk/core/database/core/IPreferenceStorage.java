package com.sensorsdata.analytics.harmony.sdk.core.database.core;

import ohos.aafwk.ability.IDataAbilityObserver;
import ohos.utils.zson.ZSONObject;

public interface IPreferenceStorage {
    boolean getBoolean(String key, boolean defaultValue);

    float getFloat(String key,float defaultValue);

    int getInt(String key,int defaultValue);

    long getLong(String key,long defaultValue);

    String getString(String key, String defaultValue);

    void putBoolean(String key, boolean value);

    void putFloat(String key, float value);

    void putInt(String key, int value);

    void putLong(String key, long value);

    void putString(String key, String value);

    void putZSONObject(String key, ZSONObject value);

    ZSONObject getZSONObject(String key);

    int remove(String key);

    void registerPreferenceChangeListener(String key, IDataAbilityObserver observer);
}
