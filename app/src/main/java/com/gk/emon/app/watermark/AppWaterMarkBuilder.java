package com.gk.emon.app.watermark;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by Gk Emon on 12/3/2019.
 */
public class AppWaterMarkBuilder {
    AppCompatActivity activity;
    int watermarkResourceID;
    int opacity;
    @ColorInt
    int defaultBackgroundColor=0x500000;
    //This is the main view resource id which we want to show as a watermark

    public static AppWaterMarkBuilder getInstance() {
        return new AppWaterMarkBuilder();
    }

    public AppWaterMarkBuilder setAppCompatActivity(@NonNull AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }

    public AppWaterMarkBuilder setWatermarkResourceID(@LayoutRes int resID, int opacity,
                                                      @ColorRes int defaultBackgroundColor) {
        this.watermarkResourceID = resID;
        this.opacity = opacity;
        try {
            this.defaultBackgroundColor = activity.getResources().getColor(defaultBackgroundColor);
        }catch (Exception ignored){}

        return this;
    }

    public AppWaterMarkBuilder setWatermarkResourceID(@LayoutRes int resID, int opacity) {
        this.watermarkResourceID = resID;
        this.opacity = opacity;
        return this;
    }

    public AppWaterMarkBuilder setWatermarkResourceID(@LayoutRes int resID) {
        this.watermarkResourceID = resID;
        return this;
    }

    public void build() {

        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(activity)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivityForResult(intent, 122);
                    return;
                }
            }

            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View oView = layoutInflater.inflate(watermarkResourceID, null);

            oView.setBackgroundColor(Color.parseColor(ColorTransparentUtils
                    .transparentColor(getBackgroundColor(oView), opacity)));


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
                            wm.addView(oView, params);
                        }

                        void onApplicationStop() {
                            wm.removeView(oView);
                        }
                    }
            ));
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
