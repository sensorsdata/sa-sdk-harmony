/*
 * Created by chenru on 2015/08/01.
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
package com.sensorsdata.analytics.harmony.sdk;


import com.sensorsdata.analytics.harmony.sdk.core.property.SAPropertyPlugin;
import ohos.utils.zson.ZSONObject;

import java.util.Map;
import java.util.Set;

public interface ISensorsDataAPI {

    /**
     * 设置当前 serverUrl
     *
     * @param serverUrl 当前 serverUrl
     */
    void setServerUrl(String serverUrl);

    /**
     * 获取当前 serverUrl
     *
     * @return 当前 serverUrl
     */
    String getServerUrl();

    /**
     * 设置是否允许请求网络，默认是 true
     *
     * @param isRequest boolean
     */
    void enableNetworkRequest(boolean isRequest);

    /**
     * 设置 flush 时网络发送策略，默认 3G、4G、5G、WI-FI 环境下都会尝试 flush
     *
     * @param networkType int 网络类型
     */
    void setFlushNetworkPolicy(int networkType);

    /**
     * 获取当前用户的 distinctId
     *
     * @return 优先返回登录 ID，登录 ID 为空时，返回匿名 ID
     */
    String getDistinctId();

    /**
     * 获取当前用户的匿名 ID
     * 若调用前未调用 {@link #identify(String)} 设置用户的匿名 ID，SDK 会优先调用 {@link }获取 Android ID，
     * 如获取的 Android ID 非法，则调用 {@link java.util.UUID} 随机生成 UUID，作为用户的匿名 ID
     *
     * @return 当前用户的匿名 ID
     */
    String getAnonymousId();

    /**
     * 重置默认匿名 ID
     */
    void resetAnonymousId();

    /**
     * 获取当前用户的 loginId
     * 若调用前未调用 {@link #login(String)} 设置用户的 loginId，会返回 null
     *
     * @return 当前用户的 loginId
     */
    String getLoginId();

    /**
     * 设置当前用户的 anonymousId。一般情况下，如果是一个注册用户，则应该使用注册系统内
     * 的 user_id，如果是个未注册用户，则可以选择一个不会重复的匿名 ID，如设备 ID 等，如果
     * 客户没有调用 identify，则使用SDK自动生成的匿名 ID
     *
     * @param anonymousId 当前用户的 anonymousId，仅接受数字、下划线和大小写字母
     */
    void identify(String anonymousId);

    /**
     * 登录，设置当前用户的 loginId
     *
     * @param loginId 当前用户的 loginId，不能为空，且长度不能大于 255
     */
    void login(String loginId);

    /**
     * 登录，设置当前用户的 loginId
     *
     * @param loginId    当前用户的 loginId，不能为空，且长度不能大于 255
     * @param properties 用户登录属性
     */
    void login(final String loginId, final ZSONObject properties);

    /**
     * 注销，清空当前用户的 loginId
     */
    void logout();

    /**
     * 记录 $AppInstall 事件，用于在 App 首次启动时追踪渠道来源，并设置追踪渠道事件的属性。
     * 注意：如果之前使用 trackInstallation 触发的激活事件，需要继续保持原来的调用，无需改成 trackAppInstall，否则会导致激活事件数据分离。
     * 这是 Sensors Analytics 进阶功能，请参考文档 https://sensorsdata.cn/manual/track_installation.html
     */
    void trackAppInstall();

    /**
     * 记录 $AppInstall 事件，用于在 App 首次启动时追踪渠道来源，并设置追踪渠道事件的属性。
     * 注意：如果之前使用 trackInstallation 触发的激活事件，需要继续保持原来的调用，无需改成 trackAppInstall，否则会导致激活事件数据分离。
     * 这是 Sensors Analytics 进阶功能，请参考文档 https://sensorsdata.cn/manual/track_installation.html
     *
     * @param properties 渠道追踪事件的属性
     */
    void trackAppInstall(ZSONObject properties);

    /**
     * 记录 $AppInstall 事件，用于在 App 首次启动时追踪渠道来源，并设置追踪渠道事件的属性。
     * 注意：如果之前使用 trackInstallation 触发的激活事件，需要继续保持原来的调用，无需改成 trackAppInstall，否则会导致激活事件数据分离。
     * 这是 Sensors Analytics 进阶功能，请参考文档 https://sensorsdata.cn/manual/track_installation.html
     *
     * @param properties      渠道追踪事件的属性
     * @param disableCallback 是否关闭这次渠道匹配的回调请求
     */
    void trackAppInstall(ZSONObject properties, boolean disableCallback);

    /**
     * 调用 track 接口，追踪一个带有属性的事件
     *
     * @param eventName  事件的名称
     * @param properties 事件的属性
     */
    void track(String eventName, ZSONObject properties);

    /**
     * 与 {@link #track(String, ZSONObject)} 类似，无事件属性
     *
     * @param eventName 事件的名称
     */
    void track(String eventName);

    /**
     * 删除事件的计时器
     *
     * @param eventName 事件名称
     */
    void removeTimer(final String eventName);

    /**
     * 停止事件计时器
     *
     * @param eventName  事件的名称，或者交叉计算场景时 trackTimerStart 的返回值
     * @param properties 事件的属性
     */
    void trackTimerEnd(final String eventName, ZSONObject properties);

    /**
     * 停止事件计时器
     *
     * @param eventName 事件的名称，或者交叉计算场景时 trackTimerStart 的返回值
     */
    void trackTimerEnd(final String eventName);

    /**
     * 清除所有事件计时器
     */
    void clearTrackTimer();

    /**
     * 将所有本地缓存的日志发送到 Sensors Analytics.
     */
    void flush();

    /**
     * 获取事件公共属性
     *
     * @return 当前所有 Super 属性
     */
    ZSONObject getSuperProperties();

    /**
     * 注册所有事件都有的公共属性
     *
     * @param superProperties 事件公共属性
     */
    void registerSuperProperties(ZSONObject superProperties);

    /**
     * 删除事件公共属性
     *
     * @param superPropertyName 事件属性名称
     */
    void unregisterSuperProperty(String superPropertyName);

    /**
     * 删除所有事件公共属性
     */
    void clearSuperProperties();

    /**
     * 设置用户的一个或多个 Profile。
     * Profile如果存在，则覆盖；否则，新创建。
     *
     * @param properties 属性列表
     */
    void profileSet(ZSONObject properties);

    /**
     * 设置用户的一个 Profile，如果之前存在，则覆盖，否则，新创建
     *
     * @param property 属性名称
     * @param value    属性的值，值的类型只允许为
     *                 {@link String}, {@link Number}, {@link java.util.Date}, {@link Boolean}, {@link ohos.utils.zson.ZSONArray}
     */
    void profileSet(String property, Object value);

    /**
     * 首次设置用户的一个或多个 Profile。
     * 与profileSet接口不同的是，如果之前存在，则忽略，否则，新创建
     *
     * @param properties 属性列表
     */
    void profileSetOnce(ZSONObject properties);

    /**
     * 设置用户的一个 Profile，如果之前存在，则覆盖，否则，新创建
     *
     * @param property 属性名称
     * @param value    属性的值，值的类型只允许为
     *                 {@link String}, {@link Number}, {@link java.util.Date}, {@link Boolean}, {@link ohos.utils.zson.ZSONArray}
     */
    void profileSetOnce(String property, Object value);

    /**
     * 给一个或多个数值类型的 Profile 增加一个数值。只能对数值型属性进行操作，若该属性
     * 未设置，则添加属性并设置默认值为 0
     *
     * @param properties 一个或多个属性集合
     */
    void profileIncrement(Map<String, ? extends Number> properties);

    /**
     * 给一个数值类型的 Profile 增加一个数值。只能对数值型属性进行操作，若该属性
     * 未设置，则添加属性并设置默认值为 0
     *
     * @param property 属性名称
     * @param value    属性的值，值的类型只允许为 {@link Number}
     */
    void profileIncrement(String property, Number value);

    /**
     * 给一个列表类型的 Profile 增加一个元素
     *
     * @param property 属性名称
     * @param value    新增的元素`
     */
    void profileAppend(String property, String value);

    /**
     * 给一个列表类型的 Profile 增加一个或多个元素
     *
     * @param property 属性名称
     * @param values   新增的元素集合
     */
    void profileAppend(String property, Set<String> values);

    /**
     * 删除用户的一个 Profile
     *
     * @param property 属性名称
     */
    void profileUnset(String property);

    /**
     * 删除用户所有 Profile
     */
    void profileDelete();

    /**
     * 初始化事件的计时器，计时单位为秒。
     *
     * @param eventName 事件的名称
     * @return 交叉计时的事件名称
     */
    String trackTimerStart(final String eventName);

    /**
     * 初始化事件的计时器，计时单位为秒。
     *
     * @param eventName 事件的名称
     * @return 交叉计时的事件名称
     */
    String trackTimerStart(final String eventName, ZSONObject properties);

    /**
     * 暂停事件计时器，计时单位为秒。
     *
     * @param eventName 事件的名称
     */
    void trackTimerPause(final String eventName);

    /**
     * 恢复事件计时器，计时单位为秒。
     *
     * @param eventName 事件的名称
     */
    void trackTimerResume(final String eventName);

    /**
     * 删除本地缓存的全部事件
     */
    void deleteAll();

    /**
     * 保存用户推送 ID 到用户表
     *
     * @param pushTypeKey 属性名称（例如 jgId）
     * @param pushId      推送 ID
     *                    使用 profilePushId("jgId",JPushInterface.getRegistrationID(this))
     */

    void profilePushId(String pushTypeKey, String pushId);

    /**
     * 删除用户设置的 pushId
     *
     * @param pushTypeKey 属性名称（例如 jgId）
     */
    void profileUnsetPushId(String pushTypeKey);

    /**
     * 设置 item
     *
     * @param itemType   item 类型
     * @param itemId     item ID
     * @param properties item 相关属性
     */
    void itemSet(String itemType, String itemId, ZSONObject properties);

    /**
     * 删除 item
     *
     * @param itemType item 类型
     * @param itemId   item ID
     */
    void itemDelete(String itemType, String itemId);

    /**
     * 返回预置属性
     *
     * @return ZSONObject 预置属性
     */
    ZSONObject getPresetProperties();

    /**
     * 添加属性插件
     *
     * @param plugin 属性插件
     */
    void addPropertyPlugin(SAPropertyPlugin plugin);
}
