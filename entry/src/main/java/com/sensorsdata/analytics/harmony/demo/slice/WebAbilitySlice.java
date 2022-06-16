package com.sensorsdata.analytics.harmony.demo.slice;

import com.sensorsdata.analytics.harmony.demo.ResourceTable;
import com.sensorsdata.analytics.harmony.sdk.SensorsDataAPI;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.webengine.WebView;

public class WebAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_slice_web);

        WebView webView = (WebView) findComponentById(ResourceTable.Id_webview);
        webView.getWebConfig().setJavaScriptPermit(true);  // 如果网页需要使用JavaScript，增加此行；如何使用JavaScript下文有详细介绍
        final String url = "http://172.21.4.195:8080/idmapping/test.html"; // EXAMPLE_URL由开发者自定义


        webView.load(url);
//        webView.setBrowserAgent(new BrowserAgent(this) {
//            @Override
//            public boolean onJsMessageShow(WebView webView, String url, String message, boolean isAlert, JsMessageResult result) {
//                SALog.i(TAG,"BrowserAgent onJsMessageShow : " + message);
//                if (isAlert) {
//                    // 将Web页面的alert对话框改为ToastDialog方式提示
//                    new ToastDialog(getApplicationContext()).setText(message).setAlignment(LayoutAlignment.CENTER).show();
//                    // 对弹框进行确认处理
//                    result.confirm();
//                    return true;
//                } else {
//                    return super.onJsMessageShow(webView, url, message, isAlert, result);
//                }
//            }
//        });
//        webView.addJsCallback("sensorsdata_get_server_url", new JsCallback() {
//            @Override
//            public String onCallback(String eventInfo) {
//                SALog.i("webview----->", SensorsAnalyticsManager.getInstance().getServerUrl());
//                return SensorsAnalyticsManager.getInstance().getServerUrl();
//            }
//        });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
