package com.sensorsdata.analytics.harmony.sdk.core.webview;

import com.sensorsdata.analytics.harmony.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SALog;
import com.sensorsdata.analytics.harmony.sdk.common.utils.SATextUtils;
import com.sensorsdata.analytics.harmony.sdk.core.SensorsAnalyticsManager;
import ohos.agp.components.webengine.BrowserAgent;
import ohos.agp.components.webengine.JsCallback;
import ohos.agp.components.webengine.WebAgent;
import ohos.agp.components.webengine.WebView;
import ohos.utils.zson.ZSONException;
import ohos.utils.zson.ZSONObject;

public class SAWebViewCallback {
    private static final String TAG = "SA.SAWebViewCallback";
    private ZSONObject properties;

    public SAWebViewCallback(WebView webView, ZSONObject properties) {
        this.properties = properties;
        addCallback(webView);
    }

    private void addCallback(WebView webView) {
        if (webView == null) {
            return;
        }
        //注入 sensorsdata_track 对象，供 Web JS SDk 触发事件
        webView.addJsCallback("sensorsdata_track", new JsCallback() {
            @Override
            public String onCallback(String eventInfo) {
                SALog.i(TAG, eventInfo);
                return null;
            }
        });
        // 注入 sensorsdata_get_server_url 对象，供 Web JS SDk 验证数据接收地址
        webView.addJsCallback("sensorsdata_get_server_url", new JsCallback() {
            @Override
            public String onCallback(String eventInfo) {
                SALog.i(TAG, SensorsAnalyticsManager.getInstance().getServerUrl());
                return SensorsAnalyticsManager.getInstance().getServerUrl();
            }
        });
    }
}
