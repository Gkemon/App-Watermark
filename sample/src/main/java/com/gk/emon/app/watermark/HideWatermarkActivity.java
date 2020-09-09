package com.gk.emon.app.watermark;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class HideWatermarkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_watermark);
        findViewById(R.id.btn_hide_watermark).setOnClickListener(view -> {

                AppWaterMarkBuilder.hideWatermark();

        });

    }
}
