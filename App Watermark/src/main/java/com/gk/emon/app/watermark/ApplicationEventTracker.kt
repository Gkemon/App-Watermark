package com.gk.emon.app.watermark

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

/** This is for observing when application is going to foreground and when backgroud */
class ApplicationEventTracker(private val eventListener: EventListener) : ActivityLifecycleCallbacks {
    private var activityReferencesOffOn = 0 //This flag is for observing when app is started and stopped.
    private var isActivityChangingConfigurations = false
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        if (++activityReferencesOffOn == 1 && !isActivityChangingConfigurations) {
            // App enters foreground
            eventListener.onApplicationStar()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        eventListener.onApplicationForeground()
    }

    override fun onActivityPaused(activity: Activity) {
        eventListener.onApplicationBackground()
    }

    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        if (--activityReferencesOffOn == 0 && !isActivityChangingConfigurations) {
            // App enters background
            eventListener.onApplicationStop()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
    abstract class EventListener {
        open fun onApplicationStop() {}
        open fun onApplicationStar() {}
        fun onApplicationForeground() {}
        fun onApplicationBackground() {}
    }
}