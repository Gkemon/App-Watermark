package com.gk.emon.app.watermark;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
//This is for observing when application is going to foreground and when backgroud

public class ApplicationEventTracker implements Application.ActivityLifecycleCallbacks {
    private int activityReferencesOffOn = 0;//This flag is for observing when app is started and stopped.
    private boolean isActivityChangingConfigurations = false;
    private  EventListener eventListener;
    public ApplicationEventTracker (EventListener eventListener){
        this.eventListener=eventListener;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++activityReferencesOffOn == 1 && !isActivityChangingConfigurations) {
            // App enters foreground
            eventListener.onApplicationStar();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
            eventListener.onApplicationForeground();
    }

    @Override
    public void onActivityPaused(Activity activity) {
            eventListener.onApplicationBackground();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferencesOffOn == 0 && !isActivityChangingConfigurations) {
            // App enters background
            eventListener.onApplicationStop();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    abstract static class  EventListener{
        void onApplicationStop() {}
        void onApplicationStar() {}
        void onApplicationForeground() {}
        void onApplicationBackground() {}

    }
}