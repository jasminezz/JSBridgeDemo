package com.example.win7.jsbridgedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {
   private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new JSBridgeWebChromeClient());
        JSBridge.register("bridge",BridgeImpl.class);
        mWebView.loadUrl("file:///android_asset/index.html");
    }
}
