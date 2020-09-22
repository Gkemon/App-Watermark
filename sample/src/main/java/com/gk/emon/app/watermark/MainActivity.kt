package com.gk.emon.app.watermark

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gk.emon.app.watermark.AppWaterMarkBuilder.doConfigure

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doConfigure()
                .setAppCompatActivity(this@MainActivity)
                .setWatermarkProperty(R.layout.layout_water_mark, 40, R.color.colorAccent)
                .showAlsoOutsideOfTheApp()
                .showWatermarkAfterConfig(object : WatermarkListener {
                    override fun onSuccess() {
                        Log.d(TAG, "Successfully showing water mark")
                    }

                    override fun onFailure(message: String?, throwable: Throwable?) {
                        Log.d(TAG, "Failed: $message")
                    }

                    override fun showLog(log: String?, throwable: Throwable?) {
                        Log.d(TAG, "Log: $log")
                    }
                })

        findViewById<View>(R.id.btn_hide_watermark).setOnClickListener {
            startActivity(Intent(this@MainActivity, HideWatermarkActivity::class.java))
        }
        findViewById<View>(R.id.btn_show_watermark).setOnClickListener {
            startActivity(Intent(this@MainActivity, ShowWatermarkActivity::class.java))
        }
    }

    companion object {
        var TAG: String = MainActivity::class.java.simpleName
    }
}