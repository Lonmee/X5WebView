package com.example.x5webview.utils.x5;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class X5ActivityTemp extends X5Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
    }
}