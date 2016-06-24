package com.example.win7.jsbridgedemo;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Created by $zjl on 2016/6/22.
 */
public class JSBridge {
    private static HashMap<String, HashMap< String, Method>> exposedMethod = new HashMap<>();

    public static void register(String exposedName, Class<? extends IBridge> clazz){
        if(!exposedMethod.containsKey(exposedName)){
            try {
                exposedMethod.put(exposedName, getAllMethod(clazz));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static HashMap<String,Method> getAllMethod(Class injectedCls) throws Exception {
        HashMap<String , Method> mMethodsMap = new HashMap<>();
        Method [] methods = injectedCls.getDeclaredMethods();
        for(Method method : methods){
            String name;
            if(method.getModifiers() != (Modifier.PUBLIC | Modifier.STATIC)|| (name = method.getName()) == null){
                continue;
            }
            Class [] parameters = method.getParameterTypes();
            if(null != parameters && parameters.length == 3){
                if(parameters[0] == WebView.class && parameters[1] == JSONObject.class && parameters[2] == Callback.class){
                    mMethodsMap.put(name,method);
                }
            }
        }
        return mMethodsMap;
    }

    public static String callJava(WebView webView, String uriString) {
        String methodName = "";
        String className = "" ;
        String param = "{}";
        String port = "";
        if(!TextUtils.isEmpty(uriString) && uriString.startsWith("JSBridge")){
            Uri uri = Uri.parse(uriString);
            className = uri.getHost();
            param = uri.getQuery();
            port = uri.getPort() + "";
            String path = uri.getPath();
            if(!TextUtils.isEmpty(path)){
                methodName = path.replace("/","");
            }
        }
        if(exposedMethod.containsKey(className)){
            HashMap< String, Method> methodHashMap = exposedMethod.get(className);
            if(methodHashMap != null && methodHashMap.size() !=0 && methodHashMap.containsKey(methodName)){
                Method method = methodHashMap.get(methodName);
                if(method != null){
                    try {
                        method.invoke(null,webView,new JSONObject(param),new Callback(webView,port));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static class Callback {
        private  Handler mHandler = new Handler(Looper.getMainLooper());
        private static final String CALLBACK_JS_FORMAT = "javascript:JSBridge.onFinish('%s',%s);";
        private String mPort;
        private WeakReference<WebView> mWebViewRef;
        public Callback(WebView view, String port) {
            mWebViewRef = new WeakReference<WebView>(view);
            mPort = port;
        }

        public void apply(JSONObject jsonObject){
            final String execJs = String.format(CALLBACK_JS_FORMAT, mPort, String.valueOf(jsonObject));
            if(mWebViewRef != null && mWebViewRef.get() != null){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebViewRef.get().loadUrl(execJs);
                    }
                });
            }
        }

    }
}

