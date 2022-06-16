package com.sensorsdata.analytics.harmony.sdk.core;

import com.sensorsdata.analytics.harmony.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SALog;
import ohos.ace.ability.LocalParticleAbility;
import ohos.utils.zson.ZSONObject;

import java.util.Set;

/**
 * 供 JS 端调用
 */
public class SALocalParticleAbility implements LocalParticleAbility {

    private static class Holder {
        private static SALocalParticleAbility _instance = new SALocalParticleAbility();
    }

    private SALocalParticleAbility() {

    }

    public static SALocalParticleAbility getInstance() {
        return Holder._instance;
    }

    public void setServerUrl(String serverUrl) {
        SensorsDataAPI.sharedInstance().setServerUrl(serverUrl);
    }

    public String getServerUrl() {
        return SensorsDataAPI.sharedInstance().getServerUrl();
    }

    public void enableNetworkRequest(boolean isRequest) {
        SensorsDataAPI.sharedInstance().enableNetworkRequest(isRequest);
    }

    public void setFlushNetworkPolicy(int networkType) {
        SensorsDataAPI.sharedInstance().setFlushNetworkPolicy(networkType);
    }

    public String getDistinctId() {
        return SensorsDataAPI.sharedInstance().getDistinctId();
    }

    public String getAnonymousId() {
        return SensorsDataAPI.sharedInstance().getAnonymousId();
    }

    public void resetAnonymousId() {
        SensorsDataAPI.sharedInstance().resetAnonymousId();
    }

    public String getLoginId() {
        return SensorsDataAPI.sharedInstance().getLoginId();
    }

    public void identify(String distinctId) {
        SensorsDataAPI.sharedInstance().identify(distinctId);
    }

    public void login(String loginId, ZSONObject properties) {
        SensorsDataAPI.sharedInstance().login(loginId, properties);
    }

    public void logout() {
        SensorsDataAPI.sharedInstance().logout();
    }

    public void trackAppInstall(ZSONObject properties) {
        SensorsDataAPI.sharedInstance().trackAppInstall(properties);
    }

    public void track(String eventName, ZSONObject properties) {
        SensorsDataAPI.sharedInstance().track(eventName, properties);
    }

    public String trackTimerStart(String eventName, ZSONObject properties) {
        return SensorsDataAPI.sharedInstance().trackTimerStart(eventName, properties);
    }

    public void trackTimerPause(String eventName) {
        SensorsDataAPI.sharedInstance().trackTimerPause(eventName);
    }

    public void trackTimerResume(String eventName) {
        SensorsDataAPI.sharedInstance().trackTimerResume(eventName);
    }

    public void trackTimerEnd(String eventName, ZSONObject properties) {
        SensorsDataAPI.sharedInstance().trackTimerEnd(eventName, properties);
    }

    public void removeTimer(String eventName) {
        SensorsDataAPI.sharedInstance().removeTimer(eventName);
    }

    public void clearTrackTimer() {
        SensorsDataAPI.sharedInstance().clearTrackTimer();
    }

    public void flush() {
        SensorsDataAPI.sharedInstance().flush();
    }

    public ZSONObject getSuperProperties() {
        return SensorsDataAPI.sharedInstance().getSuperProperties();
    }

    public void registerSuperProperties(ZSONObject superProperties) {
        SensorsDataAPI.sharedInstance().registerSuperProperties(superProperties);
    }

    public void unregisterSuperProperty(String superPropertyName) {
        SensorsDataAPI.sharedInstance().unregisterSuperProperty(superPropertyName);
    }

    public void clearSuperProperties() {
        SensorsDataAPI.sharedInstance().clearSuperProperties();
    }

    public void profileSet(ZSONObject properties) {
        SensorsDataAPI.sharedInstance().profileSet(properties);
    }

    public void profileSetOnce(ZSONObject properties) {
        SensorsDataAPI.sharedInstance().profileSetOnce(properties);
    }

    public void profileIncrement(String property, Number value) {
        SensorsDataAPI.sharedInstance().profileIncrement(property, value);
    }

    public void profileAppend(String property, Set<String> values) {
        SensorsDataAPI.sharedInstance().profileAppend(property, values);
    }

    public void profileUnset(String property) {
        SensorsDataAPI.sharedInstance().profileUnset(property);
    }

    public void profileDelete() {
        SensorsDataAPI.sharedInstance().profileDelete();
    }

    public void deleteAll() {
        SensorsDataAPI.sharedInstance().deleteAll();
    }

    public void profilePushId(String pushTypeKey, String pushId) {
        SensorsDataAPI.sharedInstance().profilePushId(pushTypeKey, pushId);
    }

    public void profileUnsetPushId(String pushTypeKey) {
        SensorsDataAPI.sharedInstance().profileUnsetPushId(pushTypeKey);
    }

    public void itemSet(String itemType, String itemId, ZSONObject properties) {
        SensorsDataAPI.sharedInstance().itemSet(itemType, itemId, properties);
    }

    public void itemDelete(String itemType, String itemId) {
        SensorsDataAPI.sharedInstance().itemDelete(itemType, itemId);
    }

    public ZSONObject getPresetProperties() {
        return SensorsDataAPI.sharedInstance().getPresetProperties();
    }

    public void showLog(String tag, String msg) {
        SALog.i(tag, msg);
    }
}
