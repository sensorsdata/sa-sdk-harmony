/*
 * Created by chenru on 2021/06/01.
 * Copyright 2015－2021 Sensors Data Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sensorsdata.analytics.harmony.sdk.common.network;


import com.sensorsdata.analytics.harmony.sdk.common.utils.SALog;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SATextUtils;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.utils.zson.ZSONException;
import ohos.utils.zson.ZSONObject;

public abstract class HttpCallback<T> {
    static EventHandler sCurrentHandler;

    public HttpCallback() {
        EventRunner runner = EventRunner.getMainEventRunner();
        sCurrentHandler = new EventHandler(runner);
    }


    void onError(final RealResponse response) {
        final String errorMessage;
        if (!SATextUtils.isEmpty(response.result)) {
            errorMessage = response.result;
        } else if (!SATextUtils.isEmpty(response.errorMsg)) {
            errorMessage = response.errorMsg;
        } else if (response.exception != null) {
            errorMessage = response.exception.toString();
        } else {
            errorMessage = "unknown error";
        }
        sCurrentHandler.postSyncTask(new Runnable() {
            @Override
            public void run() {
                onFailure(response.code, errorMessage);
                onAfter();
            }
        });
    }

    void onSuccess(RealResponse response) {
        final T obj;
        obj = onParseResponse(response.result);
        sCurrentHandler.postSyncTask(new Runnable() {
            @Override
            public void run() {
                onResponse(obj);
                onAfter();
                sCurrentHandler.getEventRunner().stop();
            }
        });
    }

    /**
     * 解析 Response，执行在子线程
     *
     * @param result 网络请求返回信息
     * @return T
     */
    public abstract T onParseResponse(String result);

    /**
     * 访问网络失败后被调用，执行在 UI 线程
     *
     * @param code         请求返回的错误 code
     * @param errorMessage 错误信息
     */
    public abstract void onFailure(int code, String errorMessage);

    /**
     * 访问网络成功后被调用，执行在 UI 线程
     *
     * @param response 处理后的对象
     */
    public abstract void onResponse(T response);

    /**
     * 访问网络成功或失败后调用
     */
    public abstract void onAfter();

    public static abstract class StringCallback extends HttpCallback<String> {
        @Override
        public String onParseResponse(String result) {
            return result;
        }
    }

    public static abstract class ZSONCallback extends HttpCallback<ZSONObject> {
        @Override
        public ZSONObject onParseResponse(String result) {
            try {
                if (!SATextUtils.isEmpty(result)) {
                    return ZSONObject.stringToZSON(result);
                }
            } catch (ZSONException e) {
                SALog.printStackTrace(e);
            }
            return null;
        }

        @Override
        public void onAfter() {

        }
    }
}