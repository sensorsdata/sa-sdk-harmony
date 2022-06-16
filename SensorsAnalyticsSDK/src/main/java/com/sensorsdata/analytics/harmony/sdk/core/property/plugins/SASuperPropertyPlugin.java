package com.sensorsdata.analytics.harmony.sdk.core.property.plugins;

import com.sensorsdata.analytics.harmony.sdk.common.utils.SAZSONUtils;
import com.sensorsdata.analytics.harmony.sdk.core.SAEventType;
import com.sensorsdata.analytics.harmony.sdk.core.database.SADataOperate;
import com.sensorsdata.analytics.harmony.sdk.core.property.SAPropertyPlugin;
import ohos.utils.zson.ZSONObject;

import java.util.Set;

public class SASuperPropertyPlugin extends SAPropertyPlugin {

    private ZSONObject mSuperProperties;

    public SASuperPropertyPlugin() {

    }

    public void registerProperties(ZSONObject superProperties) {
        initProperties();
        if (!superProperties.isEmpty()) {
            SAZSONUtils.mergeZSONObject(superProperties, mSuperProperties);
            SADataOperate.getInstance().commitSuperProperties(mSuperProperties);
        }
    }

    public void unregisterProperty(String key) {
        initProperties();
        if (mSuperProperties.containsKey(key)) {
            mSuperProperties.remove(key);
            SADataOperate.getInstance().commitSuperProperties(mSuperProperties);
        }
    }

    public ZSONObject getProperties() {
        initProperties();
        return mSuperProperties;
    }

    public void clearProperties() {
        initProperties();
        mSuperProperties.clear();
        SADataOperate.getInstance().commitSuperProperties(null);
    }

    @Override
    public void appendDynamicProperties(ZSONObject properties) {
        initProperties();
        SAZSONUtils.mergeZSONObject(mSuperProperties, properties);
    }

    @Override
    public void appendProperties(ZSONObject devicesProperties) {

    }

    @Override
    public void eventTypeFilter(Set<SAEventType> eventTypeFilter) {
        eventTypeFilter.add(SAEventType.TRACK);
        eventTypeFilter.add(SAEventType.TRACK_SIGNUP);
    }

    private void initProperties() {
        if (mSuperProperties == null) {
            mSuperProperties = SADataOperate.getInstance().getSuperProperties();
            if (mSuperProperties == null) {
                mSuperProperties = new ZSONObject();
            }
        }
    }
}
