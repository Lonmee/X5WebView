package com.example.x5webview.utils.x5;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.x5webview.R;
import com.example.x5webview.utils.shortcut.X5Pool;
import com.example.x5webview.utils.x5.core.X5JSFun;
import com.example.x5webview.utils.x5.core.X5WebView;

public class X5Activity extends AppCompatActivity {

    protected ViewGroup x5Frame;
    protected X5WebView x5wv;
    private int curSid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x5);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        try {
            if (Build.VERSION.SDK_INT >= 11) {
                getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                        android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
            Log.d("x5", e.getMessage());
        }

        x5wv = new X5WebView(this, null);
        x5Frame = findViewById(R.id.x5Frame);
        x5Frame.addView(x5wv);
        getApplicationContext();

        x5wv.addJavascriptInterface(new X5JSFun(this), "dbWebView");
        /************** testing **************/

        /************** testing ind **********/

        Intent intent = getIntent();
        int sId = intent.getIntExtra("sId", 0);
        String sName = intent.getStringExtra("sName");
        setTitle(sName);
        if (sId > 0) {
            loadUrl(sId);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int newSid = intent.getIntExtra("sId", 0);
        if (newSid > 0 && newSid != curSid) {
            loadUrl(newSid);
        }
        //startActivityForResult(new Intent(), curSid);

    }

    protected void loadUrl(int sId) {
        curSid = sId;
        String url = "http://172.16.137.84/yunyun-story-player/bin/heart/index.html?curId=1&storyId=609";
        Log.i("x5url", "loadUrl: " + url);
        x5wv.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (x5wv.canGoBack()) {
            x5wv.goBack();
        } else {
//            super.onBackPressed();
            moveTaskToBack(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        x5wv.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            x5wv.restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        x5wv.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        x5wv.onResume();
        //todo:check token
        X5Pool.getInstance().refreshOrder(123);
        hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        X5Pool.getInstance().retrieveActivity(123);
    }

    /**************************** for animate hiding ActiveBar ****************************/
    protected static final int UI_ANIMATION_DELAY = 300;
    protected final Handler mHideHandler = new Handler();

    protected final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            x5Frame.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    protected final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d("fsa:", "onPostCreate");
        super.onPostCreate(savedInstanceState);

        delayedHide(100);
    }

    protected void hide() {
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    protected void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**************************** for animate hiding ActiveBar end ************************/
}
