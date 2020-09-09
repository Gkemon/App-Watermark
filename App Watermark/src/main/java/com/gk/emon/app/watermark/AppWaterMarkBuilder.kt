package com.gk.emon.app.watermark

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.inlineactivityresult.InlineActivityResult
import com.github.florent37.inlineactivityresult.Result

/**
 * Created by Gk Emon on 12/3/2019.
 */
object AppWaterMarkBuilder {
    private var appWaterMarkBuilder: Builders? = null
    @JvmStatic
    @Synchronized
    fun doConfigure(): ActivityStep? {
        return if (appWaterMarkBuilder != null) appWaterMarkBuilder else Builders().also { appWaterMarkBuilder = it }
    }

    @Throws(Exception::class)
    fun hideWatermark() {
        if (appWaterMarkBuilder != null) appWaterMarkBuilder!!.hideWatermark() else throw Exception("AppWaterMarkBuilder is null. First configure it. ")
    }

    @Throws(Exception::class)
    fun hideWatermark(watermarkListener: WatermarkListener?) {
        if (appWaterMarkBuilder != null) appWaterMarkBuilder!!.hideWatermark(watermarkListener) else throw Exception("AppWaterMarkBuilder is null. First configure it. ")
    }

    @Throws(Exception::class)
    fun showWatermark() {
        if (appWaterMarkBuilder != null) appWaterMarkBuilder!!.showWatermark() else throw Exception("AppWaterMarkBuilder is null. First configure it. ")
    }

    @JvmStatic
    @Throws(Exception::class)
    fun showWatermark(watermarkListener: WatermarkListener?) {
        if (appWaterMarkBuilder != null) appWaterMarkBuilder!!.showWatermark(watermarkListener) else throw Exception("AppWaterMarkBuilder is null. First configure it. ")
    }

    interface ActivityStep {
        fun setAppCompatActivity(activity: AppCompatActivity): FinalStep
    }

    interface FinalStep {
        fun setWatermarkProperty(@LayoutRes resID: Int, opacity: Int,
                                 @ColorRes defaultBackgroundColor: Int): AppWaterMarkBuilderStep

        fun setWatermarkProperty(@LayoutRes resID: Int, opacity: Int): AppWaterMarkBuilderStep
        fun setWatermarkProperty(@LayoutRes resID: Int): AppWaterMarkBuilderStep
    }

    interface AppWaterMarkBuilderStep {
        fun showWatermarkAfterConfig(watermarkListener: WatermarkListener?)
        fun showWatermarkAfterConfig()
    }

    private interface WatermarkHideShowContract {
        fun showWatermark()
        fun showWatermark(watermarkListener: WatermarkListener?)
        fun hideWatermark()
        fun hideWatermark(watermarkListener: WatermarkListener?)
    }

    private class Builders : FinalStep, ActivityStep, AppWaterMarkBuilderStep, WatermarkHideShowContract {
        var wm: WindowManager? = null
        var overlaidView: View? = null

        //This is the main view resource id which we want to show as a watermark
        @LayoutRes
        var overlayLayoutID = 0

        @ColorInt
        var defaultBackgroundColor = Color.BLACK
        var opacity = 50
        private var activity: AppCompatActivity? = null
        private var watermarkListener: WatermarkListener? = null
        private var params: WindowManager.LayoutParams? = null
        override fun setAppCompatActivity(activity: AppCompatActivity): FinalStep {
            this.activity = activity
            return this
        }

        override fun setWatermarkProperty(@LayoutRes overlayLayoutID: Int,
                                          opacity: Int, @ColorRes defaultBackgroundColor: Int): AppWaterMarkBuilderStep {
            this.overlayLayoutID = overlayLayoutID
            this.opacity = opacity
            try {
                this.defaultBackgroundColor = activity!!.resources.getColor(defaultBackgroundColor)
            } catch (ignored: Exception) {
            }
            return this
        }

        override fun setWatermarkProperty(@LayoutRes overlayLayoutID: Int, opacity: Int): AppWaterMarkBuilderStep {
            this.overlayLayoutID = overlayLayoutID
            this.opacity = opacity
            return this
        }

        override fun setWatermarkProperty(@LayoutRes overlayLayoutID: Int): AppWaterMarkBuilderStep {
            this.overlayLayoutID = overlayLayoutID
            return this
        }

        private fun buildConfiguration() {
            try {
                val layoutInflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                overlaidView = layoutInflater.inflate(overlayLayoutID, null)
                try {
                    overlaidView.setBackgroundColor(Color.parseColor(ColorTransparentUtils
                            .transparentColor(getBackgroundColor(overlaidView), opacity)))
                } catch (exception: Exception) {
                    postLog("Background color not set properly.", exception)
                    overlaidView.setBackgroundColor(defaultBackgroundColor)
                }
                params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams(
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            PixelFormat.TRANSLUCENT)
                } else {
                    WindowManager.LayoutParams(
                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            PixelFormat.TRANSLUCENT)
                }
                if (wm == null) {
                    wm = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                }
            } catch (exception: Exception) {
                postFailure(exception)
            }
        }

        /** It attaches the water-mark with application life cycle. So After "AppWaterMarkBuilder" is
         * configured well,when user enter the app the water mark will be showed and water-mark will be
         * disappeared when user exist the app.
         * @param wm
         * @param overlaidView
         * @param params
         */
        private fun addWatermarkWithinApplicationLifecycle(wm: WindowManager, overlaidView: View,
                                                           params: WindowManager.LayoutParams) {
            if (activity != null) activity!!.application.registerActivityLifecycleCallbacks(ApplicationEventTracker(
                    object : ApplicationEventTracker.EventListener() {
                        override fun onApplicationStar() {
                            try {
                                wm.addView(overlaidView, params)
                            } catch (exception: Exception) {
                                postFailure(exception)
                            }
                        }

                        override fun onApplicationStop() {
                            try {
                                wm.removeView(overlaidView)
                            } catch (exception: Exception) {
                                postFailure(exception)
                            }
                        }
                    }
            ))
        }

        override fun showWatermark() {
            try {
                wm!!.addView(overlaidView, params)
                postSuccess()
            } catch (exception: Exception) {
                postFailure(exception)
            }
        }

        override fun showWatermark(watermarkListener: WatermarkListener?) {
            this.watermarkListener = watermarkListener
            showWatermark()
        }

        override fun hideWatermark(watermarkListener: WatermarkListener?) {
            if (watermarkListener != null) this.watermarkListener = watermarkListener
            hideWatermark()
        }

        override fun showWatermarkAfterConfig() {
            if (activity != null) {
                buildConfiguration()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(activity)) {
                        /** There is a bug in this library which is "onFailed()" is calling always though
                         * ACTION_MANAGE_OVERLAY_PERMISSION is given from setting screen
                         * (Result code is being 0 or Activity.RESULT_CANCELED underneath). So a quick fix
                         * is been done where.
                         */
                        InlineActivityResult(activity)
                                .startForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + activity!!.packageName)))
                                .onSuccess { result: Result? -> if (Settings.canDrawOverlays(activity)) addWatermarkWithinApplicationLifecycle(wm!!, overlaidView!!, params!!) else postLog("Settings.canDrawOverlays(activity) is false", null) }
                                .onFail { result: Result? -> if (Settings.canDrawOverlays(activity)) addWatermarkWithinApplicationLifecycle(wm!!, overlaidView!!, params!!) else postLog("Settings.canDrawOverlays(activity) is false", null) }
                        postLog("Settings.canDrawOverlays(activity) is false. Please set " +
                                "the give the overlay permission", null)
                    } else addWatermarkWithinApplicationLifecycle(wm!!, overlaidView!!, params!!)
                } else postLog("target SDK is below then 23", null)
            } else postLog("Activity is null or not set", null)
        }

        override fun hideWatermark() {
            try {
                wm!!.removeView(overlaidView)
                postSuccess()
            } catch (exception: Exception) {
                postFailure(exception)
            }
        }

        override fun showWatermarkAfterConfig(watermarkListener: WatermarkListener?) {
            this.watermarkListener = watermarkListener
            showWatermarkAfterConfig()
        }

        private fun postFailure(throwable: Throwable?) {
            if (throwable != null && watermarkListener != null) {
                watermarkListener!!.onFailure(throwable.localizedMessage, throwable)
            }
        }

        private fun postSuccess() {
            if (watermarkListener != null) watermarkListener!!.onSuccess()
        }

        private fun postLog(log: String, throwable: Throwable?) {
            if (watermarkListener != null && !TextUtils.isEmpty(log)) {
                watermarkListener!!.showLog(log, throwable)
            }
        }

        private fun getBackgroundColor(view: View?): Int {
            val drawable = view!!.background
            if (drawable is ColorDrawable) {
                return drawable.color
            }
            return defaultBackgroundColor
        }
    }
}