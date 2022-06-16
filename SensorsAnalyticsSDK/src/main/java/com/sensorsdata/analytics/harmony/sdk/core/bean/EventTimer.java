package com.sensorsdata.analytics.harmony.sdk.core.bean;

import com.sensorsdata.analytics.harmony.sdk.common.utils.SALog;
import ohos.os.ProcessManager;
import ohos.utils.zson.ZSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EventTimer {
    private final ZSONObject properties;
    private long startTime;
    private long endTime;
    private long eventAccumulatedDuration;
    private boolean isPaused = false;

    public EventTimer(ZSONObject properties, long startTime) {
        this.startTime = startTime;
        this.properties = properties;
        this.eventAccumulatedDuration = 0;
        this.endTime = -1;
    }

    public String duration() {
        if (isPaused) {
            endTime = startTime;
        } else {
            endTime = endTime < 0 ? ProcessManager.getStartRealtime() : endTime;
        }
        long duration = endTime - startTime + eventAccumulatedDuration;
        try {
            if (duration < 0 || duration > 24 * 60 * 60 * 1000) {
                return String.valueOf(0);
            }
            float durationFloat;
            durationFloat = duration / 1000.0f;
            return durationFloat < 0 ? String.valueOf(0) : String.format(Locale.CHINA, "%.3f", durationFloat);
        } catch (Exception e) {
            SALog.printStackTrace(e);
            return String.valueOf(0);
        }
    }

    long getStartTime() {
        return startTime;
    }

    void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    long getEndTime() {
        return endTime;
    }

    long getEventAccumulatedDuration() {
        return eventAccumulatedDuration;
    }

    void setEventAccumulatedDuration(long eventAccumulatedDuration) {
        this.eventAccumulatedDuration = eventAccumulatedDuration;
    }

    public void setTimerState(boolean isPaused, long elapsedRealtime) {
        this.isPaused = isPaused;
        if (isPaused) {
            eventAccumulatedDuration = eventAccumulatedDuration + elapsedRealtime - startTime;
        }
        startTime = elapsedRealtime;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public ZSONObject getProperties() {
        return properties;
    }
}
