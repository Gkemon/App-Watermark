package com.gk.emon.app.watermark;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AppWaterMarkBuilder.getInstance()
                .setAppCompatActivity(MainActivity.this)
                .setWatermarkResourceID(R.layout.activity_transparent,50,R.color.colorAccent)
                .build();


        findViewById(R.id.bt1).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SecondActivity.class)));
        findViewById(R.id.bt2).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ThirdActivity.class)));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 122) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                        AppWaterMarkBuilder.getInstance()
                                .setAppCompatActivity(MainActivity.this)
                                .setWatermarkResourceID(R.layout.activity_transparent)
                                .build();
                }
            }
        }
    }
}
