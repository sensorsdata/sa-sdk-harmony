package com.sensorsdata.analytics.harmony.sdk.core.database.core;

import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.utils.zson.ZSONObject;

public interface ISqliteStorage {
    int deleteAll(String tableName);

    int delete(String tableName, DataAbilityPredicates predicates);

    int insert(String tableName, ValuesBucket valuesBucket);

    int bulkInsert(String tableName, Object data);

    String[] query(String tableName, int limit);

    String[] query(String tableName);

    int queryRowCount(String tableName);
}
