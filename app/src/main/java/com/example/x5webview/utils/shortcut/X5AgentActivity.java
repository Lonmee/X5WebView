package com.example.x5webview.utils.shortcut;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class X5AgentActivity extends AppCompatActivity {
    private static final String Tag = "X5AgentActivity";
    DBShortcutMgr scMgr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scMgr = new DBShortcutMgr();
        Class<?> aClass = null;

        try {
            aClass = Class.forName(X5Pool.getInstance().getNextActivity(getIntent().getIntExtra("sId", 0)));
        } catch (Exception e) {
            Log.e(this.Tag, "no Class");
        }

        if (aClass != null) {
            startActivity(new Intent(this, aClass).putExtras(getIntent()));
        }
        finish();
        //For testing
        //DBShortcutMgr.getInstance().createShortcut(getApplicationContext(), "xxx", 123, "");
    }
}
