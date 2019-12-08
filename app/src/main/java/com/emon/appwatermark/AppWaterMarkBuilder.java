package com.emon.appwatermark;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by Gk Emon on 12/3/2019.
 */
public class AppWaterMarkBuilder {
    AppCompatActivity activity;
    int watermarkResourceID;//This is the main view resource id which we want to show as a watermark

    public static AppWaterMarkBuilder getInstance() {
        return new AppWaterMarkBuilder();
    }

    public AppWaterMarkBuilder setAppCompatActivity(AppCompatActivity activity){
        this.activity=activity;
        return this;
    }

    public AppWaterMarkBuilder seWatermarkResourceID(int resID){
        this.watermarkResourceID=resID;
        return this;
    }

    public void build()  {

        if(activity==null){
            throw new NullPointerException("AppCompatActivity is null");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent,122);
                return;
            }
        }



        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View oView = layoutInflater.inflate(watermarkResourceID, null);
        final WindowManager.LayoutParams params;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    PixelFormat.TRANSLUCENT);
        }
        final WindowManager wm = (WindowManager) activity.getSystemService(WINDOW_SERVICE);


        activity.getApplication().registerActivityLifecycleCallbacks(new ApplicationEventTracker(
                new ApplicationEventTracker.EventListener() {

                    void onApplicationStar() {
                        wm.addView(oView,params);
                    }

                    void onApplicationStop() {
                        wm.removeView(oView);
                    }
                }
        ));


    }

}
