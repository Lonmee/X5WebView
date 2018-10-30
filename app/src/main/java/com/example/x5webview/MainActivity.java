package com.example.x5webview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.x5webview.utils.shortcut.X5AgentActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.logBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLog();
            }
        });

        findViewById(R.id.x5Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goX5();
            }
        });
    }

    protected void goLog() {
        startActivity(new Intent(this, LoginActivity.class));
        String.valueOf(5);
    }

    protected void goX5() {
        Intent intent = new Intent(this, X5AgentActivity.class);
        intent.putExtra("sId", 123);
        intent.putExtra("sName", "我是测试游戏");
        startActivity(intent);
    }
}
