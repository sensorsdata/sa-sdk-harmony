<img src="https://ow-file.sensorsdata.cn/www/home/header/sensors_header_icon.svg" width="200" >

[![License](https://img.shields.io/github/license/sensorsdata/sa-sdk-android.svg)](https://github.com/sensorsdata/sa-sdk-android/blob/master/LICENSE)
[![GitHub release](https://img.shields.io/github/tag/sensorsdata/sa-sdk-cpp.svg?label=release)](https://github.com/sensorsdata/sa-sdk-cpp/releases)
[![GitHub release date](https://img.shields.io/github/release-date/sensorsdata/sa-sdk-cpp.svg)](https://github.com/sensorsdata/sa-sdk-cpp/releases)

## 神策简介

[**神策数据**](https://www.sensorsdata.cn/)
（Sensors Data），隶属于神策网络科技（北京）有限公司，是一家专业的大数据分析服务公司，大数据分析行业开拓者，为客户提供深度用户行为分析平台、以及专业的咨询服务和行业解决方案，致力于帮助客户实现数据驱动。神策数据立足大数据及用户行为分析的技术与实践前沿，业务现已覆盖以互联网、金融、零售快消、高科技、制造等为代表的十多个主要行业、并可支持企业多个职能部门。公司总部在北京，并在上海、深圳、合肥、武汉等地拥有本地化的服务团队，覆盖东区及南区市场；公司拥有专业的服务团队，为客户提供一对一的客户服务。公司在大数据领域积累的核心关键技术，包括在海量数据采集、存储、清洗、分析挖掘、可视化、智能应用、安全与隐私保护等领域。 [**More**](https://www.sensorsdata.cn/about/aboutus.html)


## SDK 简介

SensorsAnalytics SDK 是国内第一家开源商用版用户行为采集 SDK，目前支持代码埋点、全埋点、App 点击图、可视化全埋点等。目前已累计有 1500 多家付费客户，2500+ 的 App 集成使用，作为 App 数据采集利器，致力于帮助客户挖掘更多的商业价值，为其精准运营和业务支撑提供了可靠的数据来源。其采集全面而灵活、性能良好，并一直保持稳定的迭代，经受住了时间和客户的考验。
```
「鸿蒙数据采集 SDK」目前仅支持代码埋点
```
## 基本要求
```sh
HarmonyOS SDK JAVA Api Version 不能低于 6。
```

## 集成方式
### 引入 SDK
```sh
dependencies {
   //添加 Sensors Analytics SDK 依赖
   implementation "com.sensorsdata.analytics.harmony:SensorsAnalyticsSDK:1.0.0" 
}
```
###权限配置说明

SDK 共需要三个权限（非敏感），可在 config.json 中进行配置

|  权限   | 用途  |
|  ----  | ----  |
| INTERNET  | 必须权限，允许应用发送统计数据，SDK 发送埋点数据需要此权限 |
| GET_NETWORK_INFO  | 必须权限，允许应用检测蜂窝网络状态，SDK 会根据网络状态选择是否发送数据  |
| GET_WIFI_INFO  |必须权限 <br> 1.允许应用检测 WIFI 网络状态，SDK 会根据网络状态选择是否发送数据 <br>  2.允许应用获取 MAC 地址，采用 App 内推广 时会用到此权限 |
```sh
  "module": {
    ...
    "reqPermissions": [
      {
        "name": "ohos.permission.GET_WIFI_INFO"
      },
      {
        "name": "ohos.permission.GET_NETWORK_INFO"
      },
      {
        "name": "ohos.permission.INTERNET"
      }
    ]
	... 
```


## 新书推荐

| [《数据驱动：从方法到实践》](https://item.jd.com/12322322.html) | [《Android 全埋点解决方案》](https://item.jd.com/12574672.html) | [《iOS 全埋点解决方案》](https://item.jd.com/12867068.html)
| ------ | ------ | ------ |

## License
Copyright 2015－2024 Sensors Data Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
