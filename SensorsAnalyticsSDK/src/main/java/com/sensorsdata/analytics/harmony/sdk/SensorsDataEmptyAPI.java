package com.sensorsdata.analytics.harmony.sdk;

import com.sensorsdata.analytics.harmony.sdk.core.property.SAPropertyPlugin;
import ohos.utils.zson.ZSONObject;

import java.util.Map;
import java.util.Set;


public class SensorsDataEmptyAPI implements ISensorsDataAPI {

    @Override
    public void setServerUrl(String serverUrl) {

    }

    @Override
    public String getServerUrl() {
        return null;
    }

    @Override
    public void enableNetworkRequest(boolean isRequest) {

    }

    @Override
    public void setFlushNetworkPolicy(int networkType) {

    }

    @Override
    public String getDistinctId() {
        return null;
    }

    @Override
    public String getAnonymousId() {
        return null;
    }

    @Override
    public void resetAnonymousId() {

    }

    @Override
    public String getLoginId() {
        return null;
    }

    @Override
    public void identify(String distinctId) {

    }

    @Override
    public void login(String loginId) {

    }

    @Override
    public void login(String loginId, ZSONObject properties) {

    }

    @Override
    public void logout() {

    }

    @Override
    public void trackAppInstall(ZSONObject properties) {

    }

    @Override
    public void trackAppInstall(ZSONObject properties, boolean disableCallback) {

    }

    @Override
    public void trackAppInstall() {

    }

    @Override
    public void track(String eventName, ZSONObject properties) {

    }

    @Override
    public void track(String eventName) {

    }

    @Override
    public void removeTimer(String eventName) {

    }

    @Override
    public void trackTimerEnd(String eventName, ZSONObject properties) {

    }

    @Override
    public void trackTimerEnd(String eventName) {

    }

    @Override
    public void clearTrackTimer() {

    }

    @Override
    public void flush() {

    }

    @Override
    public ZSONObject getSuperProperties() {
        return new ZSONObject();
    }

    @Override
    public void registerSuperProperties(ZSONObject superProperties) {

    }

    @Override
    public void unregisterSuperProperty(String superPropertyName) {

    }

    @Override
    public void clearSuperProperties() {

    }

    @Override
    public void profileSet(ZSONObject properties) {

    }

    @Override
    public void profileSet(String property, Object value) {

    }

    @Override
    public void profileSetOnce(ZSONObject properties) {

    }

    @Override
    public void profileSetOnce(String property, Object value) {

    }

    @Override
    public void profileIncrement(String property, Number value) {

    }

    @Override
    public void profileIncrement(Map<String, ? extends Number> properties) {

    }

    @Override
    public void profileAppend(String property, String value) {

    }

    @Override
    public void profileAppend(String property, Set<String> values) {

    }

    @Override
    public void profileUnset(String property) {

    }

    @Override
    public void profileDelete() {

    }

    @Override
    public String trackTimerStart(String eventName) {
        return null;
    }

    @Override
    public String trackTimerStart(String eventName, ZSONObject properties) {
        return null;
    }

    @Override
    public void trackTimerPause(String eventName) {

    }

    @Override
    public void trackTimerResume(String eventName) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void profilePushId(String pushTypeKey, String pushId) {

    }

    @Override
    public void profileUnsetPushId(String pushTypeKey) {

    }

    @Override
    public void itemSet(String itemType, String itemId, ZSONObject properties) {

    }

    @Override
    public void itemDelete(String itemType, String itemId) {

    }

    @Override
    public ZSONObject getPresetProperties() {
        return null;
    }

    @Override
    public void addPropertyPlugin(SAPropertyPlugin plugin) {

    }
}
