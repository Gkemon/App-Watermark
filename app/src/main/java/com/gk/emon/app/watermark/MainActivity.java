package com.gk.emon.app.watermark;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppWaterMarkBuilder.getInstance()
                .setAppCompatActivity(MainActivity.this)
                .setWatermarkProperty(R.layout.activity_transparent)
                .showWatermark(new WatermarkListener() {
                    @Override
                    public void onSuccess() {
                        super.onSuccess();
                    }

                    @Override
                    public void onFailure(String message, Throwable throwable) {
                        super.onFailure(message,throwable);
                    }

                    @Override
                    public void showLog(String log, @Nullable Throwable throwable) {
                        super.showLog(log, throwable);
                    }
                });

        findViewById(R.id.bt1).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SecondActivity.class)));

        findViewById(R.id.bt2).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ThirdActivity.class)));

    }
}
