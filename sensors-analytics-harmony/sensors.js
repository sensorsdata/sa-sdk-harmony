import prompt from '@system.prompt'

const TAG = "SA.JS_BRIDGE"

var sensors = createLocalParticleAbility("com.sensorsdata.analytics.harmony.sdk.core.SALocalParticleAbility")

var sa = {}
sa.NetworkType = {
    'TYPE_NONE': 0,
    'TYPE_2G': 1,
    'TYPE_3G': 1 << 1,
    'TYPE_4G': 1 << 2,
    'TYPE_WIFI': 1 << 3,
    'TYPE_5G': 1 << 4,
    'TYPE_ALL': 0xFF,
}

sa.setServerUrl = function (url) {
    if (sensors) {
        try {
            sensors.setServerUrl(url);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.getServerUrl = async function () {
    if (sensors) {
        return await sensors.getServerUrl();
    }
    return "";
}

sa.enableNetworkRequest = function () {
    if (sensors) {
        try {
            sensors.enableNetworkRequest(false);
        } catch (error) {
            showLog(error.message);
        }
    }
}
sa.setFlushNetworkPolicy = function (networkType) {
    if (sensors) {
        try {
            sensors.setFlushNetworkPolicy(networkType);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.getDistinctId = async function () {
    if (sensors) {
        return await sensors.getDistinctId();
    }
    return "";
}

sa.getAnonymousId = async function () {
    if (sensors) {
        return await sensors.getAnonymousId();
    }
    return "";
}

sa.getLoginId = async function () {
    if (sensors) {
        return await sensors.getLoginId();
    }
    return "";
}

sa.resetAnonymousId = function () {
    if (sensors) {
        try {
            sensors.resetAnonymousId();
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.identify = function (anonymousId) {
    if (sensors) {
        try {
            sensors.identify(anonymousId);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.login = function (loginId) {
    if (sensors) {
        try {
            sensors.login(loginId);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.logout = function () {
    if (sensors) {
        try {
            sensors.logout();
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.trackAppInstall = function (properteis) {
    if (sensors) {
        try {
            sensors.trackAppInstall(properteis);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.track = function (eventName, properties) {
    if (sensors) {
        try {
            sensors.track(eventName, properties);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.trackTimerStart = async function (eventName, properties) {
    if (sensors) {
        return await sensors.trackTimerStart(eventName, properties);
    }
    return "";
}

sa.trackTimerPause = function (eventName) {
    if (sensors) {
        return sensors.trackTimerPause(eventName);
    }
}

sa.trackTimerResume = function (eventName) {
    if (sensors) {
        return sensors.trackTimerResume(eventName);
    }
}

sa.trackTimerEnd = function (eventName, properties) {
    if (sensors) {
        sensors.trackTimerEnd(eventName, properties);
    }
}

sa.clearTrackTimer = function () {
    if (sensors) {
        sensors.clearTrackTimer();
    }
}
sa.removeTimer = function (eventName) {
    if (sensors) {
        sensors.removeTimer(eventName);
    }
}

sa.flush = function () {
    if (sensors) {
        sensors.flush();
    }
}

sa.deleteAll = function () {
    if (sensors) {
        sensors.deleteAll();
    }
}

sa.registerSuperProperties = function (properties) {
    if (sensors) {
        try {
            sensors.registerSuperProperties(properties);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.unregisterSuperProperty = function (propertyName) {
    if (sensors) {
        try {
            sensors.unregisterSuperProperty(propertyName);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.getSuperProperties = async function () {
    if (sensors) {
        return await sensors.getSuperProperties();
    }
    return "{}";
}

sa.clearSuperProperties = function () {
    if (sensors) {
        try {
            sensors.clearSuperProperties();
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.profileSet = function (properties) {
    if (sensors) {
        try {
            sensors.profileSet(properties);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.profileSetOnce = function (properties) {
    if (sensors) {
        try {
            sensors.profileSetOnce(properties);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.profileIncrement = function (property, value) {
    if (sensors) {
        try {
            sensors.profileIncrement(property, value);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.profileAppend = function (property, strList) {
    if (sensors) {
        try {
            sensors.profileAppend(property, strList);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.profileUnset = function (property) {
    if (sensors) {
        try {
            sensors.profileUnset(property);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.profileDelete = function () {
    if (sensors) {
        try {
            sensors.profileDelete();
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.profilePushId = function (pushTypeKey, pushId) {
    if (sensors) {
        try {
            sensors.profilePushId(pushTypeKey, pushId);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.itemSet = function (itemType, itemId, properties) {
    if (sensors) {
        try {
            sensors.itemSet(itemType, itemId, properties);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.itemDelete = function (itemType, itemId) {
    if (sensors) {
        try {
            sensors.itemDelete(itemType, itemId);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.profileUnsetPushId = function (pushTypeKey) {
    if (sensors) {
        try {
            sensors.profileUnsetPushId(pushTypeKey);
        } catch (error) {
            showLog(error.message);
        }
    }
}

sa.getPresetProperties = async function () {
    if (sensors) {
        return await sensors.getPresetProperties();
    }
    return "{}";
}

function showLog(message) {
    if (sensors) {
        try {
            sensors.showLog(TAG, message);
        } catch (error) {

        }
    }
}

export default sa;