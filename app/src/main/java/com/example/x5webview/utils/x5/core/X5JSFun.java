package com.example.x5webview.utils.x5.core;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class X5JSFun implements WebViewJavaScriptFunction {
    @JavascriptInterface
    public void onJsFunctionCalled(String tag) {
        Log.d("x5", "onJsFunctionCalled: " + tag);
    }

}
