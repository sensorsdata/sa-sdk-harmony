declare type PropertiesType = String | number | boolean | Array<String>;

declare type PropertiesObjectType = { [key: String]: PropertiesType }

declare namespace sensors {

/**
     * 设置当前 serverUrl
     *
     * @param serverUrl 当前 serverUrl
     */
    function setServerUrl(url: String): void

    /**
     * 获取当前 serverUrl
     *
     * @return 当前 serverUrl
     */
    function getServerUrl(): Promise<String>

    /**
     * 设置是否允许请求网络，默认是 true
     *
     * @param isRequest boolean
     */
    function enableNetworkRequest(isEnable): void

    /**
   * 设置 flush 时网络发送策略，默认 3G、4G、WI-FI 环境下都会尝试 flush
   *
   * @param NetWorkType 网络发送策略
   */
    function setFlushNetworkPolicy(networkType: NetWorkType): void

    /**
     * 获取当前用户的 distinctId
     *
     * @return 优先返回登录 ID，登录 ID 为空时，返回匿名 ID
     */
    function getDistinctId(): Promise<String>

    /**
     * 获取当前用户的匿名 ID
     * 若调用前未调用 {@link #identify(String)} 设置用户的匿名 ID，SDK 会优先调用 {@link }获取 Android ID，
     * 如获取的 Android ID 非法，则调用 {@link java.util.UUID} 随机生成 UUID，作为用户的匿名 ID
     *
     * @return 当前用户的匿名 ID
     */
    function getAnonymousId(): Promise<String>

    /**
     * 重置默认匿名 ID
     */
    function resetAnonymousId(): void

    /**
     * 获取当前用户的 loginId
     * 若调用前未调用 {@link #login(String)} 设置用户的 loginId，会返回 null
     *
     * @return 当前用户的 loginId
     */
    function getLoginId(): Promise<String>

    /**
     * 设置当前用户的 anonymousId。一般情况下，如果是一个注册用户，则应该使用注册系统内
     * 的 user_id，如果是个未注册用户，则可以选择一个不会重复的匿名 ID，如设备 ID 等，如果
     * 客户没有调用 identify，则使用SDK自动生成的匿名 ID
     *
     * @param anonymousId 当前用户的 anonymousId，仅接受数字、下划线和大小写字母
     */
    function identify(anonymousId: String): void

    /**
     * 登录，设置当前用户的 loginId
     *
     * @param loginId 当前用户的 loginId，不能为空，且长度不能大于 255
     * @param properties 用户登录属性 可为空
     */
    function login(loginId: String, properties: PropertiesObjectType | null): void

    /**
     * 注销，清空当前用户的 loginId
     */
    function logout(): void

    /**
     * 记录 $AppInstall 事件，用于在 App 首次启动时追踪渠道来源，并设置追踪渠道事件的属性。
     * 注意：如果之前使用 trackInstallation 触发的激活事件，需要继续保持原来的调用，无需改成 trackAppInstall，否则会导致激活事件数据分离。
     * 这是 Sensors Analytics 进阶功能，请参考文档 https://sensorsdata.cn/manual/track_installation.html
     *
     * @param properties 渠道追踪事件的属性 可为空
     */
    function trackAppInstall(properties: PropertiesObjectType | null): void

    /**
     * 调用 track 接口，追踪一个带有属性的事件
     *
     * @param eventName 事件的名称
     * @param properties 事件的属性
     */
    function track(eventName: String, properties: PropertiesObjectType | null): void


    /**
     * 初始化事件的计时器，计时单位为秒。
     *
     * @param eventName 事件的名称
     * @return 交叉计时的事件名称
     */
    function trackTimerStart(eventName: String, properties: PropertiesObjectType | null): Promise<String>

    /**
     * 暂停事件计时器，计时单位为秒。
     *
     * @param eventName 事件的名称
     */
    function trackTimerPause(eventName: String): void

    /**
     * 恢复事件计时器，计时单位为秒。
     *
     * @param eventName 事件的名称
     */
    function trackTimerResume(eventName: String): void

    /**
     * 停止事件计时器
     *
     * @param eventName 事件的名称，或者交叉计算场景时 trackTimerStart 的返回值
     * @param properties 事件的属性
     */
    function trackTimerEnd(eventName: String, properties: PropertiesObjectType | null): void

    /**
     * 清除所有事件计时器
     */
    function clearTrackTimer(): void

    /**
     * 删除事件的计时器
     *
     * @param eventName 事件名称
     */
    function removeTimer(eventName: String): void

    /**
     * 将所有本地缓存的日志发送到 Sensors Analytics.
     */
    function flush(): void

    /**
     * 删除本地缓存的全部事件
     */
    function deleteAll(): void

    /**
     * 获取事件公共属性
     *
     * @return 当前所有 Super 属性
     */
    function getSuperProperties(): Promise<PropertiesObjectType>

    /**
     * 注册所有事件都有的公共属性
     *
     * @param superProperties 事件公共属性
     */
    function registerSuperProperties(superProperties: PropertiesObjectType): void

    /**
     * 删除事件公共属性
     *
     * @param superPropertyName 事件属性名称
     */
    function unregisterSuperProperty(superPropertyName: String): void

    /**
     * 删除所有事件公共属性
     */
    function clearSuperProperties(): void
    /**
     * 设置用户的一个 Profile，如果之前存在，则覆盖，否则，新创建
     *
     * @param properties 属性对象
     */
    function profileSet(properties: PropertiesObjectType): void

    /**
     * 设置用户的一个 Profile，如果之前存在，则覆盖，否则，新创建
     *
     * @param properties 属性对象
     */
    function profileSetOnce(properties: PropertiesObjectType): void

    /**
     * 给一个或多个数值类型的 Profile 增加一个数值。只能对数值型属性进行操作，若该属性
     * 未设置，则添加属性并设置默认值为 0
     *
     * @param property 属性名称
     * @param value    属性的值，值的类型只允许为 {Number}
     */
    function profileIncrement(property: String, value: Number): void

    /**
   * 给一个列表类型的 Profile 增加一个元素.
   *
   * @param property 属性名称
   * @param strList 属性值
   */
    function profileAppend(property: string, strList: Array<String>): void

    /**
     * 删除用户的一个 Profile
     *
     * @param property 属性名称
     */
    function profileUnset(property: String): void

    /**
     * 删除用户所有 Profile
     */
    function profileDelete(): void

    /**
     * 保存用户推送 ID 到用户表
     *
     * @param pushTypeKey 属性名称（例如 jgId）
     * @param pushId      推送 ID
     *                    使用 profilePushId("jgId",JPushInterface.getRegistrationID(this))
     */
    function profilePushId(pushTypeKey: String, pushId: String): void

    /**
     * 删除用户设置的 pushId
     *
     * @param pushTypeKey 属性名称（例如 jgId）
     */
    function profileUnsetPushId(pushTypeKey: String): void

    /**
     * 设置 item
     *
     * @param itemType   item 类型
     * @param itemId     item ID
     * @param properties item 相关属性
     */
    function itemSet(itemType: String, itemId: String, properties: PropertiesObjectType): void

    /**
     * 删除 item
     *
     * @param itemType item 类型
     * @param itemId   item ID
     */
    function itemDelete(itemType: String, itemId: String): void

    /**
     * 返回预置属性
     *
     * @return Object 预置属性
     */
    function getPresetProperties(): Promise<Object>

    enum NetworkType {
        'TYPE_NONE',
        'TYPE_2G',
        'TYPE_3G',
        'TYPE_4G',
        'TYPE_WIFI',
        'TYPE_5G',
        'TYPE_ALL'
    }
}

export default sensors;