package com.example.x5webview.utils.x5.core;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.example.x5webview.utils.x5.X5Activity;

public class X5JSFun implements WebViewJavaScriptFunction {
    private X5Activity x5Instance;
    public X5JSFun(X5Activity instance) {
        x5Instance = instance;
    }

    @JavascriptInterface

    public void onJsFunctionCalled(String tag) {
        Log.d("x5", "onJsFunctionCalled: " + tag);
        switch (tag) {
            case "xx":
                break;
            case "oo":
                break;
            default:
                break;

        }
    }

    @JavascriptInterface
    public void msgForRN(final String msgType, final String params) {
        switch (msgType) {
            case "saveCamera":
                Log.d(msgType, msgType);
                break;
        }
    }
}
