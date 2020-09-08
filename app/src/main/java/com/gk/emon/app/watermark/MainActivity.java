package com.gk.emon.app.watermark;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppWaterMarkBuilder.doConfigure()
                .setAppCompatActivity(MainActivity.this)
                .setWatermarkProperty(R.layout.layout_water_mark)
                .showWatermarkAfterConfig(new WatermarkListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(String message, Throwable throwable) {

                    }

                    @Override
                    public void showLog(String log, @Nullable Throwable throwable) {

                    }
                });

        findViewById(R.id.btn_hide_watermark).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HideWatermarkActivity.class)));

        findViewById(R.id.btn_show_watermark).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ShowWatermarkActivity.class)));

    }
}
