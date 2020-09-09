package com.gk.emon.app.watermark

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gk.emon.app.watermark.AppWaterMarkBuilder.showWatermark

class ShowWatermarkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_watermark)
        findViewById<View>(R.id.btn_show_watermark).setOnClickListener {
            showWatermark(object : WatermarkListener {
                override fun onSuccess() {
                    Log.d(MainActivity.TAG, "Successfully showing water mark")
                }

                override fun onFailure(message: String?, throwable: Throwable?) {
                    Log.d(MainActivity.TAG, "Failed: $message")
                }

                override fun showLog(log: String?, throwable: Throwable?) {
                    Log.d(MainActivity.TAG, "Log: $log")
                }
            })
        }
    }
}