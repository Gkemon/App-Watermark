package com.gk.emon.app.watermark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        AppWaterMarkBuilder.doConfigure()
                .setAppCompatActivity(MainActivity.this)
                .setWatermarkProperty(R.layout.layout_water_mark, 40, R.color.colorAccent)
                .showWatermarkAfterConfig(new WatermarkListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG,"Successfully showing water mark");
                    }

                    @Override
                    public void onFailure(String message, Throwable throwable) {
                        Log.d(TAG,"Failed: "+ message);
                    }

                    @Override
                    public void showLog(String log, @Nullable Throwable throwable) {
                        Log.d(TAG,"Log: "+ log);
                    }
                });

        findViewById(R.id.btn_hide_watermark).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HideWatermarkActivity.class)));

        findViewById(R.id.btn_show_watermark).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ShowWatermarkActivity.class)));

    }
}
