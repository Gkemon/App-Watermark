package com.gk.emon.app.watermark

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gk.emon.app.watermark.AppWaterMarkBuilder.hideWatermark

class HideWatermarkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hide_watermark)
        findViewById<View>(R.id.btn_hide_watermark).setOnClickListener {hideWatermark() }
    }
}