package com.flight.movie.infra.master.money

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener


object InstallManager {

    private val TAG: String = "InstallManager"
    private var conditions = listOf(
        "apps.facebook.com",
        "fb4a",
        "gclid",
        "not%20set",
        "youtubeads",
        "bytedance",
        "google/cpc"
    )
    private var retryCount = 0

    //utm_source=apps.facebook.com；gclid；utm_source=(not%20set)&utm_medium=(not%20set)；
    private var shouldRun: Boolean = false
    private var testRunB = false

    fun init(context: Context, initComplete: Runnable) {
        val isReview = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) != 0
        Log.d(TAG, "init: isReview = $isReview")
        if (isReview) {
            shouldRun = false
            initComplete.run()
            AnalysisUtils.logEvent("app_review")
            return
        }

        if (ShareHelper.hasEnable()) {
            shouldRun = ShareHelper.getEnable()
            Log.d(TAG, "init: ShareHelper.getEnable = $shouldRun")
            val from = if (shouldRun) "B" else "A"
            AnalysisUtils.logEvent("app_launch_local_$from")
            initComplete.run()
            return
        }
        val referrerClient = InstallReferrerClient.newBuilder(context).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        // Connection established.
                        val response = referrerClient.installReferrer
                        val referrerUrl = response.installReferrer
                        val enableSdk =
                            conditions.any { str -> referrerUrl.contains(str) }
                        ShareHelper.setEnable(enableSdk)
                        Log.d(
                            TAG,
                            "onInstallReferrerSetupFinished: enableSdk=$enableSdk, referrerUrl=$referrerUrl"
                        )
                        shouldRun = enableSdk
                        initComplete.run()
                        val from = if (shouldRun) "B" else "A"
                        AnalysisUtils.logEvent("app_launch_Referrer$from")

                    }

                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        // API not available on the current Play Store app.
                        shouldRun = false
                        initComplete.run()
                        Log.d(
                            TAG,
                            "onInstallReferrerSetupFinished: --> FEATURE_NOT_SUPPORTED"
                        )
                    }

                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        // Connection couldn't be established.
                        shouldRun = false
                        initComplete.run()
                        Log.d(
                            TAG,
                            "onInstallReferrerSetupFinished: --> SERVICE_UNAVAILABLE"
                        )
                    }

                    InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR -> {
                        shouldRun = false
                        initComplete.run()
                        Log.d(TAG, "onInstallReferrerSetupFinished: --> DEVELOPER_ERROR")
                    }

                    InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED -> {
                        shouldRun = false
                        initComplete.run()
                        Log.d(
                            TAG,
                            "onInstallReferrerSetupFinished: --> SERVICE_DISCONNECTED"
                        )
                    }

                    else -> {
                        initComplete.run()
                    }
                }

            }

            override fun onInstallReferrerServiceDisconnected() {
                if (retryCount < 5) {
                    retryCount += 1
                    referrerClient.startConnection(this)
                }
            }

        })
    }


    fun getRunB() = testRunB || shouldRun

    fun setRunB() {
        testRunB = true
    }
}