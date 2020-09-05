package com.gk.emon.app.watermark;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.florent37.inlineactivityresult.InlineActivityResult;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by Gk Emon on 12/3/2019.
 */
public class AppWaterMarkBuilder {

    private static Builders appWaterMarkBuilder;

    public synchronized static ActivityStep getInstance() {
        if (appWaterMarkBuilder != null) return appWaterMarkBuilder;
        else return appWaterMarkBuilder = new Builders();
    }


    public interface ActivityStep {
        FinalStep setAppCompatActivity(@NonNull AppCompatActivity activity);
    }

    public interface FinalStep {
        AppWaterMarkBuilderStep setWatermarkProperty(@LayoutRes int resID, int opacity,
                                                     @ColorRes int defaultBackgroundColor);

        AppWaterMarkBuilderStep setWatermarkProperty(@LayoutRes int resID, int opacity);

        AppWaterMarkBuilderStep setWatermarkProperty(@LayoutRes int resID);
    }

    public interface AppWaterMarkBuilderStep {
        void showWatermark();

        void showWatermark(WatermarkListener watermarkListener);
    }


    public static class Builders implements FinalStep, ActivityStep, AppWaterMarkBuilderStep {
        //This is the main view resource id which we want to show as a watermark
        @LayoutRes
        int overlayLayoutID;
        int opacity=50;
        @ColorInt
        int defaultBackgroundColor = Color.BLACK;
        private AppCompatActivity activity;
        private WatermarkListener watermarkListener;

        public FinalStep setAppCompatActivity(@NonNull AppCompatActivity activity) {
            this.activity = activity;
            return this;
        }

        @Override
        public AppWaterMarkBuilderStep setWatermarkProperty(@LayoutRes int overlayLayoutID,
                                                            int opacity, @ColorRes int defaultBackgroundColor) {
            this.overlayLayoutID = overlayLayoutID;
            this.opacity = opacity;
            try {
                this.defaultBackgroundColor = activity.getResources().getColor(defaultBackgroundColor);
            } catch (Exception ignored) {

            }
            return this;
        }

        public AppWaterMarkBuilderStep setWatermarkProperty(@LayoutRes int overlayLayoutID, int opacity) {
            this.overlayLayoutID = overlayLayoutID;
            this.opacity = opacity;
            return this;
        }

        public AppWaterMarkBuilderStep setWatermarkProperty(@LayoutRes int overlayLayoutID) {
            this.overlayLayoutID = overlayLayoutID;
            return this;
        }

        private void build() {
            try {
                LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View oView = layoutInflater.inflate(overlayLayoutID, null);

                try {
                    oView.setBackgroundColor(Color.parseColor(ColorTransparentUtils
                            .transparentColor(getBackgroundColor(oView), opacity)));
                } catch (Exception exception) {
                    postLog("Background color not set properly.", exception);
                    oView.setBackgroundColor(defaultBackgroundColor);
                }


                final WindowManager.LayoutParams params;


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    params = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            PixelFormat.TRANSLUCENT);
                } else {
                    params = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            PixelFormat.TRANSLUCENT);
                }
                final WindowManager wm = (WindowManager) activity.getSystemService(WINDOW_SERVICE);


                activity.getApplication().registerActivityLifecycleCallbacks(new ApplicationEventTracker(
                        new ApplicationEventTracker.EventListener() {

                            void onApplicationStar() {
                                try {
                                    wm.addView(oView, params);
                                } catch (Exception exception) {
                                    postFailure(exception);
                                }

                            }

                            void onApplicationStop() {
                                try {
                                    wm.removeView(oView);
                                } catch (Exception exception) {
                                    postFailure(exception);
                                }
                            }
                        }
                ));
            }catch (Exception exception){
                postFailure(exception);
            }

        }

        @Override
        public void showWatermark() {
            if (activity != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(activity)) {

                        /* There is a bug in this library which is "onFailed()" is calling always though
                         * ACTION_MANAGE_OVERLAY_PERMISSION is given from setting screen
                         * (Result code is being 0 or Activity.RESULT_CANCELED underneath). So a quick fix
                         * is been done where.
                         */

                        new InlineActivityResult(activity)
                                .startForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + activity.getPackageName())))
                                .onSuccess(result -> {
                                    if (Settings.canDrawOverlays(activity)) build();
                                    else
                                        postLog("Settings.canDrawOverlays(activity) is false", null);
                                })
                                .onFail(result -> {
                                    if (Settings.canDrawOverlays(activity)) build();
                                    else
                                        postLog("Settings.canDrawOverlays(activity) is false", null);
                                });

                        postLog("Settings.canDrawOverlays(activity) is false. Please set " +
                                "the give the overlay permission", null);

                    }else build();
                } else postLog("target SDK is below then 23", null);

            }

        }

        @Override
        public void showWatermark(WatermarkListener watermarkListener) {
            this.watermarkListener = watermarkListener;
            showWatermark();
        }

        private void postFailure(Throwable throwable) {
            if(throwable!=null&&watermarkListener!=null){
                watermarkListener.onFailure(throwable.getLocalizedMessage(),throwable);
            }
        }

        private void postLog(String log, Throwable throwable) {
            if(watermarkListener!=null&& !TextUtils.isEmpty(log)){
                watermarkListener.showLog(log,throwable);
            }
        }

        private int getBackgroundColor(View view) {
            Drawable drawable = view.getBackground();
            if (drawable instanceof ColorDrawable) {
                ColorDrawable colorDrawable = (ColorDrawable) drawable;
                return colorDrawable.getColor();
            }
            return defaultBackgroundColor;
        }
    }


}
