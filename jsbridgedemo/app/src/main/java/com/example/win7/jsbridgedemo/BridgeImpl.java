package com.example.win7.jsbridgedemo;

import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by $zjl on 2016/6/22.
 */
public class BridgeImpl extends IBridge{
    public static void showToast(WebView webView, JSONObject param, final JSBridge.Callback callback){
        String message = param.optString("msg");
        Toast.makeText(webView.getContext(),message,Toast.LENGTH_LONG).show();
        if(null != callback){
            try {
                JSONObject object = new JSONObject();
                object.put("key","value");
                object.put("key1","vaule1");
                callback.apply(getJSONObject(0,"ok",object));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static JSONObject getJSONObject(int code, String msg, JSONObject result) {
        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("msg",msg);
            object.putOpt("result",result);
            return object;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void testThread(WebView webView, JSONObject param, final JSBridge.Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    JSONObject object = new JSONObject();
                    object.put("key", "value");
                    callback.apply(getJSONObject(0, "ok", object));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
