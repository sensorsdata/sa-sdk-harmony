// @ts-nocheck
/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import prompt from '@system.prompt'
import deviceInfo from '@system.device';

const injectRef = Object.getPrototypeOf(global) || global

injectRef.regeneratorRuntime = require('@babel/runtime/regenerator')
import sensors from "../../sensors/sensors.js";


export default {
    setServerUrl: function () {
        try {
            sensors.setServerUrl("http://www.baidu.com");
        } catch (pluginError) {
            console.log(pluginError.message);
        }
    },
    getServerUrl: function () {
        try {
            sensors.getServerUrl().then((res) => {
                this.showToast(res);
            })
        } catch (pluginError) {
            this.showToast(pluginError.message);
        }
    },
    trackAppInstall: function () {
        sensors.trackAppInstall({
            hello: "hello"
        }, false);
    },
    track: function () {
        sensors.track("hello", {
            hello: "hello",super_key1:"track"
        });
    },
    enableNetworkRequest: function () {
        sensors.enableNetworkRequest(false)
    },
    setFlushNetworkPolicy: function () {
        sensors.setFlushNetworkPolicy(sensors.NetworkType.TYPE_WIFI);
    },
    getDistinctId: function () {
        sensors.getDistinctId().then(distinctId => {
            this.showToast(distinctId)
        })
    },
    getAnonymousId: function () {
        sensors.getAnonymousId().then(anonymousId => {
            this.showToast(anonymousId)
        })
    },
    resetAnonymousId: function () {
        sensors.resetAnonymousId("AnonymousId");
    },
    getLoginId: function () {
        sensors.getLoginId().then(loginId => {
            this.showToast(loginId)
        })
    },
    identify: function () {
        sensors.identify("identify");
    },
    login: function () {
        sensors.login("subProcess");
    },
    logout: function () {
        sensors.logout();
    },
    trackTimerStart: function () {
        sensors.trackTimerStart("trackTimer", {
            trackTimerStart: "start", replace: "start"
        });
    },
    trackTimerPause: function () {
        sensors.trackTimerPause("trackTimer");
    },
    trackTimerResume: function () {
        sensors.trackTimerResume("trackTimer");
    },
    trackTimerEnd: function () {
        sensors.trackTimerEnd("trackTimer", {
            trackTimerEnd: "end", replace: "end"
        });
    },
    clearTrackTimer: function () {
        sensors.clearTrackTimer();
    },
    removeTimer: function (eventName) {
        sensors.removeTimer(eventName);
    },
    flush: function () {
        sensors.flush();
    },
    deleteAll: function () {
        sensors.deleteAll();
    },
    getSuperProperties: function () {
        sensors.getSuperProperties().then(pro=>{
            this.showToast(JSON.stringify(pro));
        })
    },
    registerSuperProperties: function () {
        sensors.registerSuperProperties({superKey1:"value",superKey2:"value"});
    },
    unregisterSuperProperty: function () {
        sensors.unregisterSuperProperty("superKey1");
    },
    clearSuperProperties: function () {
        sensors.clearSuperProperties();
    },
    profileSet: function () {
        sensors.profileSet({
            profileSet: "profileSet"
        });
    },
    profileSetOnce: function () {
        sensors.profileSetOnce({
            profileSetOnce: "profileSetOnce"
        });
    },
    profileIncrement: function () {
        sensors.profileIncrement("profileIncrement", 2);
    },
    profileAppend: function () {
        sensors.profileAppend("profileAppend",["a", "b", "c"]);
    },
    profileUnset: function () {
        sensors.profileUnset("profileSet");
    },
    profileDelete: function () {
        sensors.profileDelete();
    },
    profilePushId: function () {
        sensors.profilePushId("pushTypeKey", "pushId");
    },
    profileUnsetPushId: function () {
        sensors.profileUnsetPushId("pushTypeKey");
    },
    itemSet: function () {
        sensors.itemSet("itemType", "itemId", {
            item: "itemValue"
        });
    },
    itemDelete: function () {
        sensors.itemDelete("itemType", "itemId");
    },
    getPresetProperties: function () {
        sensors.getPresetProperties().then(pro => {
            this.showToast(JSON.stringify(pro));
        })
    },
    showToast: function (msg) {
        prompt.showToast({
            message: msg
        });
    }
}
