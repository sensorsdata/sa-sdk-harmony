package com.sensorsdata.analytics.harmony.demo.property;

import com.sensorsdata.analytics.harmony.sdk.core.property.SAPropertyPlugin;
import com.sensorsdata.analytics.harmony.sdk.core.property.SAPropertyPluginPriority;
import ohos.utils.zson.ZSONObject;

public class HeightProperty extends SAPropertyPlugin {


    @Override
    public SAPropertyPluginPriority priority() {
        return SAPropertyPluginPriority.HIGH;
    }

    @Override
    public void appendProperties(ZSONObject properties) {
        properties.put("level", "height");
    }
}
