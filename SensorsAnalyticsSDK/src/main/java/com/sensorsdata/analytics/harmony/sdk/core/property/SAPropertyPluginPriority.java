package com.sensorsdata.analytics.harmony.sdk.core.property;

public enum SAPropertyPluginPriority {
    DEFAULT(500),
    HIGH(750),
    Low(250);

    private int priority;

    SAPropertyPluginPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
