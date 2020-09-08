package com.gk.emon.app.watermark;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ShowWatermarkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_watermark);
        findViewById(R.id.btn_show_watermark).setOnClickListener(view -> {
            try {
                AppWaterMarkBuilder.showWatermark(new WatermarkListener() {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
