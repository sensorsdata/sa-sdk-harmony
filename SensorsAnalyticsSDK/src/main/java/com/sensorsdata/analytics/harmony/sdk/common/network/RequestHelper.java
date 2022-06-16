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


import javax.net.ssl.SSLSocketFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import static com.sensorsdata.analytics.harmony.sdk.common.utils.Base64Coder.CHARSET_UTF8;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;

public class RequestHelper {
    //重定向 URL
    private boolean isRedirected = false;


    /**
     * 网络请求
     *
     * @param url        url
     * @param paramsMap  键值对参数
     * @param headerMap  请求头键值对
     * @param retryCount 重试次数
     * @param callBack   请求回调
     */
    public void execute(HttpMethod method, String url, Map<String, String> paramsMap, Map<String, String> headerMap, int retryCount, SSLSocketFactory sslSocketFactory, HttpCallback callBack) {
        switch (method) {
            case GET:
                urlHttpGet(url, paramsMap, headerMap, retryCount, sslSocketFactory, callBack);
                break;
            case POST:
                urlHttpPost(url, paramsMap, headerMap, retryCount, sslSocketFactory, callBack);
                break;
        }
    }

    /**
     * 网络请求
     *
     * @param url       url
     * @param paramsMap 键值对参数
     * @param headerMap 请求头键值对
     */
    public RealResponse executeSync(HttpMethod method, String url, Map<String, String> paramsMap, Map<String, String> headerMap, SSLSocketFactory sslSocketFactory) {
        switch (method) {
            case GET:
                return syncHttpGet(url, paramsMap, headerMap, sslSocketFactory);
            case POST:
                return syncHttpPost(url, paramsMap, headerMap, sslSocketFactory);
        }
        return new RealResponse();
    }

    /**
     * GET 请求
     *
     * @param url              url
     * @param paramsMap        键值对参数
     * @param headerMap        请求头键值对
     * @param sslSocketFactory SSLSocketFactory
     */
    private RealResponse syncHttpGet(final String url, final Map<String, String> paramsMap, final Map<String, String> headerMap, SSLSocketFactory sslSocketFactory) {
        return getResponse(getUrl(url, paramsMap), null, headerMap, sslSocketFactory);
    }

    /**
     * POST 请求
     *
     * @param url              url
     * @param paramsMap        键值对参数
     * @param headerMap        请求头键值对
     * @param sslSocketFactory
     */
    private RealResponse syncHttpPost(final String url, final Map<String, String> paramsMap,
                                      final Map<String, String> headerMap, SSLSocketFactory sslSocketFactory) {
        return getResponse(url, paramsMap, headerMap, sslSocketFactory);
    }


    /**
     * GET 请求
     *
     * @param url              url
     * @param paramsMap        键值对参数
     * @param headerMap        请求头键值对
     * @param retryCount       重试次数
     * @param sslSocketFactory SSLSocketFactory
     * @param callBack         请求回调
     */
    private void urlHttpGet(final String url, final Map<String, String> paramsMap, final Map<String, String> headerMap, final int retryCount, SSLSocketFactory sslSocketFactory, final HttpCallback callBack) {
        final int requestCount = retryCount - 1;
        HttpTaskManager.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                RealResponse response = new RealRequest().getData(getUrl(url, paramsMap), headerMap, sslSocketFactory);
                if (response.code == HTTP_OK || response.code == HTTP_NO_CONTENT) {
                    if (callBack != null) {
                        callBack.onSuccess(response);
                    }
                } else if (!isRedirected && HttpUtils.needRedirects(response.code)) {
                    isRedirected = true;
                    urlHttpGet(response.location, paramsMap, headerMap, retryCount, sslSocketFactory, callBack);
                } else {
                    if (requestCount != 0) {
                        urlHttpGet(url, paramsMap, headerMap, requestCount, sslSocketFactory, callBack);
                    } else {
                        if (callBack != null) {
                            callBack.onError(response);
                        }
                    }
                }
            }
        });
    }

    /**
     * POST 请求
     *
     * @param url              url
     * @param paramsMap        键值对参数
     * @param headerMap        请求头键值对
     * @param retryCount       重试次数
     * @param sslSocketFactory
     * @param callBack         请求回调
     */
    private void urlHttpPost(final String url, final Map<String, String> paramsMap,
                             final Map<String, String> headerMap, final int retryCount,
                             SSLSocketFactory sslSocketFactory, final HttpCallback callBack) {
        final int requestCount = retryCount - 1;
        HttpTaskManager.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                RealResponse response = getResponse(url, paramsMap, headerMap, sslSocketFactory);
                if (response.code == HTTP_OK || response.code == HTTP_NO_CONTENT) {
                    if (callBack != null) {
                        callBack.onSuccess(response);
                    }
                } else if (!isRedirected && HttpUtils.needRedirects(response.code)) {
                    isRedirected = true;
                    urlHttpPost(response.location, paramsMap, headerMap, retryCount, sslSocketFactory, callBack);
                } else {
                    if (requestCount != 0) {
                        urlHttpPost(url, paramsMap, headerMap, requestCount, sslSocketFactory, callBack);
                    } else {
                        if (callBack != null) {
                            callBack.onError(response);
                        }
                    }
                }
            }
        });
    }


    private RealResponse getResponse(final String url, final Map<String, String> paramsMap,
                                     final Map<String, String> headerMap, SSLSocketFactory sslSocketFactory) {
        return new RealRequest().postData(url, getPostBody(paramsMap), null, headerMap, sslSocketFactory);
    }

    /**
     * GET 请求 url 拼接
     *
     * @param path      请求地址
     * @param paramsMap 参数键值对参数
     * @return GET 请求 url 链接
     */
    private String getUrl(String path, Map<String, String> paramsMap) {
        if (path != null && paramsMap != null) {
            if (!path.contains("?")) {
                path = path + "?";
            } else {
                path = path + ("&");
            }
            for (String key : paramsMap.keySet()) {
                path = path + key + "=" + paramsMap.get(key) + "&";
            }
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * 根据参数得到 body
     *
     * @param params 键值对参数
     * @return 请求 body
     */
    private String getPostBody(Map<String, String> params) {
        if (params != null) {
            return getPostBodyFormParamsMap(params);
        }
        return null;
    }

    /**
     * 根据键值对参数得到 body
     *
     * @param params 键值对参数
     * @return 请求 body
     */
    private String getPostBodyFormParamsMap(Map<String, String> params) {
        if (params != null) {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        result.append("&");
                    }
                    result.append(URLEncoder.encode(entry.getKey(), CHARSET_UTF8));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue(), CHARSET_UTF8));
                }
                return result.toString();
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
        return null;
    }

    public static class Builder {

        private HttpMethod httpMethod;
        private String httpUrl;
        private Map<String, String> paramsMap;
        private String jsonData;
        private Map<String, String> headerMap;
        private HttpCallback callBack;
        private SSLSocketFactory sslSocketFactory;
        private int retryCount = 1;

        public Builder(HttpMethod method, String url) {
            this.httpMethod = method;
            this.httpUrl = url;
        }

        public Builder params(Map<String, String> paramsMap) {
            this.paramsMap = paramsMap;
            return this;
        }

        public Builder jsonData(String data) {
            this.jsonData = data;
            return this;
        }

        public Builder header(Map<String, String> headerMap) {
            this.headerMap = headerMap;
            return this;
        }

        public Builder callback(HttpCallback callBack) {
            this.callBack = callBack;
            return this;
        }

        public Builder retryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder SSLSocketFactory(SSLSocketFactory socketFactory) {
            this.sslSocketFactory = socketFactory;
            return this;
        }

        public void execute() {
            new RequestHelper().execute(httpMethod, httpUrl, paramsMap, headerMap, retryCount, sslSocketFactory, callBack);
        }

        public RealResponse executeSync() {
            return new RequestHelper().executeSync(httpMethod, httpUrl, paramsMap, headerMap, sslSocketFactory);
        }
    }
}