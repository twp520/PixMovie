package com.flight.movie.infra.master.money

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.flight.movie.infra.master.MainActivity
import com.flight.movie.infra.master.ui.EXTRA_COLD_START
import com.flight.movie.infra.master.ui.personal.LanguageActivity
import com.flight.movie.infra.master.ui.start.StartActivity

object ActivityUtil {
    var activityCount = 0
    private var mainLaunch = false

    fun initApp(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is MainActivity) {
                    mainLaunch = true
                }
            }

            override fun onActivityStarted(activity: Activity) {
                activityCount++

                if (activityCount == 1) {
                    if (mainLaunch) {
                        //热启动
                        if (activity !is StartActivity) {
                            hotLoading(activity)
                        }
                    } else {
                        //冷启动

                    }
                }
            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                activityCount--
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                if (activity is MainActivity) {
                    mainLaunch = false
                }
            }

        })
    }

    fun startToMain(activity: Activity) {
        if (!mainLaunch) {
            activity.startActivity(Intent(activity, LanguageActivity::class.java).apply {
                putExtra(EXTRA_COLD_START, true)
            })
        }
        activity.finish()
    }

    private fun hotLoading(activity: Activity) {
        activity.startActivity(Intent(activity, StartActivity::class.java))
    }
}