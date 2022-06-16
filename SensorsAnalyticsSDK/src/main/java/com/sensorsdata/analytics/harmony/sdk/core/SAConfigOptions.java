/*
 * Created by chenru on 2021/06/02.
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

package com.sensorsdata.analytics.harmony.sdk.core;


import com.sensorsdata.analytics.harmony.sdk.common.utils.SALog;

import javax.net.ssl.SSLSocketFactory;

public final class SAConfigOptions implements Cloneable {

    /**
     * 设置 SSLSocketFactory
     */
    SSLSocketFactory mSSLSocketFactory;

    /**
     * 数据上报服务器地址
     */
    String mServerUrl;

    /**
     * 两次数据发送的最小时间间隔，单位毫秒
     */
    int mFlushInterval = 15000;

    /**
     * 本地缓存日志的最大条目数
     */
    int mFlushBulkSize = 100;

    /**
     * 本地缓存上限值，单位 byte，默认为 32MB：32 * 1024 * 1024
     */
    long mMaxCacheSize = 32 * 1024 * 1024L;

    /**
     * 是否开启打印日志
     */
    boolean mLogEnabled;

    /**
     * 网络上传策略
     */
    int mNetworkTypePolicy = SensorsNetworkType.TYPE_3G | SensorsNetworkType.TYPE_4G | SensorsNetworkType.TYPE_WIFI | SensorsNetworkType.TYPE_5G;

    /**
     * AnonymousId，匿名 ID
     */
    String mAnonymousId;

    /**
     * 私有构造函数
     */
    private SAConfigOptions() {
    }

    /**
     * 获取 SAOptionsConfig 实例
     *
     * @param serverUrl，数据上报服务器地址
     */
    public SAConfigOptions(String serverUrl) {
        this.mServerUrl = serverUrl;
    }

    /**
     * 设置数据上报地址
     *
     * @param serverUrl，数据上报地址
     * @return SAOptionsConfig
     */
    public SAConfigOptions setServerUrl(String serverUrl) {
        this.mServerUrl = serverUrl;
        return this;
    }

    /**
     * 设置数据的网络上传策略
     *
     * @param networkTypePolicy 数据的网络上传策略
     * @return SAOptionsConfig
     */
    public SAConfigOptions setNetworkTypePolicy(int networkTypePolicy) {
        this.mNetworkTypePolicy = networkTypePolicy;
        return this;
    }

    /**
     * 设置匿名 ID
     *
     * @param anonymousId 匿名 ID
     * @return SAOptionsConfig
     */
    public SAConfigOptions setAnonymousId(String anonymousId) {
        this.mAnonymousId = anonymousId;
        return this;
    }

    /**
     * 设置 SSLSocketFactory，HTTPS 请求连接时需要使用
     *
     * @param SSLSocketFactory 证书
     * @return SAConfigOptions
     */
    public SAConfigOptions setSSLSocketFactory(SSLSocketFactory SSLSocketFactory) {
        this.mSSLSocketFactory = SSLSocketFactory;
        return this;
    }

    /**
     * 设置两次数据发送的最小时间间隔，最小值 5 秒
     *
     * @param flushInterval 时间间隔，单位毫秒
     * @return SAOptionsConfig
     */
    public SAConfigOptions setFlushInterval(int flushInterval) {
        this.mFlushInterval = Math.max(5 * 1000, flushInterval);
        return this;
    }

    /**
     * 设置本地缓存日志的最大条目数
     *
     * @param flushBulkSize 缓存数目
     * @return SAOptionsConfig
     */
    public SAConfigOptions setFlushBulkSize(int flushBulkSize) {
        this.mFlushBulkSize = Math.max(50, flushBulkSize);
        return this;
    }

    /**
     * 设置本地缓存上限值，单位 byte，默认为 32MB：32 * 1024 * 1024，最小 16MB：16 * 1024 * 1024，若小于 16MB，则按 16MB 处理。
     *
     * @param maxCacheSize 单位 byte
     * @return SAOptionsConfig
     */
    public SAConfigOptions setMaxCacheSize(long maxCacheSize) {
        this.mMaxCacheSize = Math.max(16 * 1024 * 1024, maxCacheSize);
        return this;
    }

    /**
     * 是否打印日志
     *
     * @param enableLog 是否开启打印日志
     * @return SAOptionsConfig
     */
    public SAConfigOptions enableLog(boolean enableLog) {
        this.mLogEnabled = enableLog;
        SALog.setEnableLog(enableLog);
        return this;
    }

    @Override
    public SAConfigOptions clone() throws CloneNotSupportedException {
        return (SAConfigOptions) super.clone();
    }
}