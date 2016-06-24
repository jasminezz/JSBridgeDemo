package com.example.win7.jsbridgedemo;

import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by $zjl on 2016/6/22.
 */
public class JSBridgeWebChromeClient extends WebChromeClient {
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        result.confirm(JSBridge.callJava(view,message));
        return true;
    }
}
