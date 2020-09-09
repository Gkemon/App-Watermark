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
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.inlineactivityresult.InlineActivityResult

/**
 * Created by Gk Emon on 12/3/2019.
 */
object AppWaterMarkBuilder {
    private const val URL_FIRST_CONFIG = "https://github.com/Gkemon/App-Watermark"
    private const val ERROR_MESSAGE_FIRST_CONFIG_MISSING = "AppWaterMarkBuilder is null or " +
            "not configured well. First configure it." +
            " Go to here to see how to config it first : " + URL_FIRST_CONFIG;
    private var appWaterMarkBuilder: Builders

    init {
        appWaterMarkBuilder = Builders()
    }

    @JvmStatic
    @Synchronized
    fun doConfigure(): ActivityStep {
        return appWaterMarkBuilder
    }

    @JvmStatic
    fun hideWatermark() {
        if (appWaterMarkBuilder.isConfiguredWell())
            appWaterMarkBuilder.hideWatermark() else
            throw IllegalStateException(ERROR_MESSAGE_FIRST_CONFIG_MISSING)

    }

    @JvmStatic
    fun hideWatermark(watermarkListener: WatermarkListener) {
        if (appWaterMarkBuilder.isConfiguredWell())
            appWaterMarkBuilder.hideWatermark(watermarkListener) else
            throw IllegalStateException(ERROR_MESSAGE_FIRST_CONFIG_MISSING)
    }

    @JvmStatic
    fun showWatermark() {
        if (appWaterMarkBuilder.isConfiguredWell())
            appWaterMarkBuilder.showWatermark() else
            throw Exception(ERROR_MESSAGE_FIRST_CONFIG_MISSING)
    }

    @JvmStatic
    fun showWatermark(watermarkListener: WatermarkListener) {
        if (appWaterMarkBuilder.isConfiguredWell())
            appWaterMarkBuilder.showWatermark(watermarkListener) else
            throw Exception(ERROR_MESSAGE_FIRST_CONFIG_MISSING)
    }

    interface ActivityStep {
        fun setAppCompatActivity(activity: AppCompatActivity): FinalStep
    }

    interface FinalStep {
        fun setWatermarkProperty(@LayoutRes overlayLayoutID: Int,@IntRange(from = 0,to = 100 )opacity: Int,
                                 @ColorRes defaultBackgroundColor: Int): AppWaterMarkBuilderStep
        fun setWatermarkProperty(@LayoutRes overlayLayoutID: Int,@IntRange(from = 0,to = 100 ) opacity: Int): AppWaterMarkBuilderStep
        fun setWatermarkProperty(@LayoutRes overlayLayoutID: Int): AppWaterMarkBuilderStep
    }

    interface AppWaterMarkBuilderStep {
        fun showWatermarkAfterConfig(watermarkListener: WatermarkListener)
        fun showWatermarkAfterConfig()
    }

    private interface WatermarkHideShowContract {
        fun showWatermark()
        fun showWatermark(watermarkListener: WatermarkListener)
        fun hideWatermark()
        fun hideWatermark(watermarkListener: WatermarkListener)
    }

    class Builders : FinalStep, ActivityStep, AppWaterMarkBuilderStep, WatermarkHideShowContract {
        private var wm: WindowManager? = null
        private lateinit var overlaidView: View
        /**This is the main view resource id which we want to show as a watermark*/
        @LayoutRes
        var overlayLayoutID = 0
        @ColorInt
        var defaultBackgroundColor = Color.BLACK
        @IntRange(from = 0,to = 100 )
        var opacity = DEFAULT_OPACITY
        private var activity: AppCompatActivity? = null
        private var watermarkListener: WatermarkListener? = null
        private var params: WindowManager.LayoutParams? = null
        private var isConfigured: Boolean = false
        override fun setAppCompatActivity(activity: AppCompatActivity): FinalStep {
            this.activity = activity
            return this
        }

        fun isConfiguredWell(): Boolean {
            return isConfigured
        }

        companion object {
            /** Opacity must be in between 0~100* otherwise it doesn't work*/
            @IntRange(from = 0,to = 100 )
            const val DEFAULT_OPACITY=50
        }

        override fun setWatermarkProperty(@LayoutRes overlayLayoutID: Int,
                                          opacity: Int, @ColorRes defaultBackgroundColor: Int): AppWaterMarkBuilderStep {
            this.overlayLayoutID = overlayLayoutID
            this.opacity = opacity
            try {
                this.defaultBackgroundColor = activity!!.resources.getColor(defaultBackgroundColor)
            } catch (ignored: Exception) {
                val stackTraceElement = StackTraceElement(AppWaterMarkBuilder::class.simpleName,
                        "setWatermarkProperty",
                        AppWaterMarkBuilder::class.simpleName + ".kt", 119)
                watermarkListener?.showLog("An error occurred in setWatermarkProperty (Line: " +
                        stackTraceElement + ") while setting \"defaultBackgroundColor\" property." +
                        "The error message is " + ignored.localizedMessage + ".", ignored)
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
                    /**defaultBackgroundColor not black means user set a default color as watermark
                     * background.If default color is black that means a default color is not set.
                     * So then it should be get the background color from the inflated view from layout
                     * resource id which will be shown as the water mark. If all of these approach get any exception
                     * then black color with 50% opacity (default opacity) will be set*/
                    if(defaultBackgroundColor!=Color.BLACK){
                        overlaidView.setBackgroundColor(Color.parseColor(ColorTransparentUtils
                                .transparentColor(defaultBackgroundColor, opacity)))
                    }else overlaidView.setBackgroundColor(Color.parseColor(ColorTransparentUtils
                            .transparentColor(getBackgroundColor(overlaidView), opacity)))

                } catch (exception: Exception) {
                    val errorLine = StackTraceElement(AppWaterMarkBuilder::class.simpleName,
                            "setWatermarkProperty",
                            AppWaterMarkBuilder::class.simpleName + ".kt", 154).fileName
                    postLog("Background color not set properly. (Line: $errorLine)", exception)
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

                /** Alternative of null check by if else in kotlin*/
                wm?.let { isConfigured = true } ?: let {
                    wm = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    isConfigured = true
                }

            } catch (exception: Exception) {
                isConfigured = false
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
        private fun addWatermarkWithinApplicationLifecycle(wm: WindowManager?, overlaidView: View,
                                                           params: WindowManager.LayoutParams?) {
            if (activity != null) activity!!.application.registerActivityLifecycleCallbacks(ApplicationEventTracker(
                    object : ApplicationEventTracker.EventListener() {
                        override fun onApplicationStar() {
                            try {
                                wm!!.addView(overlaidView, params)
                            } catch (exception: Exception) {
                                postFailure(exception)
                            }
                        }

                        override fun onApplicationStop() {
                            try {
                                wm!!.removeView(overlaidView)
                            } catch (exception: Exception) {
                                postFailure(exception)
                            }
                        }
                    }
            ))
        }

        override fun showWatermark() {
            try {
                wm?.addView(overlaidView, params)
                postSuccess()
            } catch (exception: Exception) {
                postFailure(exception)
            }
        }

        override fun showWatermark(watermarkListener: WatermarkListener) {
            this.watermarkListener = watermarkListener
            showWatermark()
        }

        override fun hideWatermark(watermarkListener: WatermarkListener) {
            this.watermarkListener = watermarkListener
            hideWatermark()
        }

        override fun showWatermarkAfterConfig() {
            activity?.let {
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
                                .onSuccess {
                                    if (Settings.canDrawOverlays(activity))
                                        addWatermarkWithinApplicationLifecycle(wm!!, overlaidView, params!!)
                                    else postLog("Settings.canDrawOverlays(activity) is false", null)
                                }
                                .onFail {
                                    if (Settings.canDrawOverlays(activity))
                                        addWatermarkWithinApplicationLifecycle(wm!!, overlaidView, params!!)
                                    else postLog("Settings.canDrawOverlays(activity) is false", null)
                                }
                        postLog("Settings.canDrawOverlays(activity) is false. Please set " +
                                "the give the overlay permission", null)
                    } else addWatermarkWithinApplicationLifecycle(wm!!, overlaidView, params!!)
                } else postLog("target SDK is below then 23", null)
            } ?: postLog("Activity is null or not set", null)

        }

        override fun hideWatermark() {
            try {
                wm!!.removeView(overlaidView)
                postSuccess()
            } catch (exception: Exception) {
                postFailure(exception)
            }
        }

        override fun showWatermarkAfterConfig(watermarkListener: WatermarkListener) {
            this.watermarkListener = watermarkListener
            showWatermarkAfterConfig()
        }

        private fun postFailure(throwable: Throwable) {
            watermarkListener?.onFailure(throwable.localizedMessage, throwable)
        }

        private fun postSuccess() {
            watermarkListener?.onSuccess()
        }

        private fun postLog(log: String, throwable: Throwable?) {
            if (!TextUtils.isEmpty(log)) {
                watermarkListener?.showLog(log, throwable)
            }
        }

        private fun getBackgroundColor(view: View): Int {
            val drawable = view.background
            if (drawable is ColorDrawable) {
                return drawable.color
            }
            return defaultBackgroundColor
        }
    }
}