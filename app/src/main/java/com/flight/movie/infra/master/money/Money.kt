package com.flight.movie.infra.master.money

import android.content.Context
import com.flight.movie.infra.master.R
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus


/**
 * create by colin
 * 2024/7/18
 *
 * Hold Splash Inter AD, this ad is important,need preload
 */
object Money {

    private val moneyScope = MainScope() + SupervisorJob()
    lateinit var splashInterLoader: InterLoader
    lateinit var guideInterLoader: InterLoader
    lateinit var languageNativeLoader: NativeLoader
    lateinit var guideNativeLoader: NativeLoader

    fun init(context: Context) {
        AnalysisUtils.logEvent("app_launch")
        InstallManager.init(context) {
        }
        ShareHelper.initClickCount()
        MobileAds.initialize(context)
        // MobileAds.setRequestConfiguration(
        //     RequestConfiguration.Builder()
        //         .setTestDeviceIds(listOf("8410F190F374DF4E365D72FD74327C5F"))
        //         .build()
        // )

        splashInterLoader = InterLoader(
            context.getString(R.string.inter_test),
            moneyScope,
            AnalysisUtils.FROM_SPLASH_INTER
        )
        guideInterLoader = InterLoader(
            context.getString(R.string.inter_test),
            moneyScope,
            AnalysisUtils.FROM_GUIDE_INTER
        )
        languageNativeLoader = NativeLoader(
            context.getString(R.string.native_test), moneyScope,
            AnalysisUtils.FROM_LANGUAGE_NATIVE
        )
        guideNativeLoader = NativeLoader(
            context.getString(R.string.native_test), moneyScope,
            AnalysisUtils.FROM_GUIDE_NATIVE
        )
        splashInterLoader.load(context.applicationContext)
        guideInterLoader.load(context.applicationContext)
        languageNativeLoader.refreshAd(context.applicationContext, null)
        guideNativeLoader.refreshAd(context.applicationContext, null)

    }


    fun prepareLoadSplash(context: Context) {
        if (splashInterLoader.shouldReLoad()) {
            splashInterLoader.load(context)
        }
        if (guideInterLoader.shouldReLoad() && InstallManager.getRunB()) {
            guideInterLoader.load(context.applicationContext)
            languageNativeLoader.refreshAd(context.applicationContext, null)
            guideNativeLoader.refreshAd(context.applicationContext, null)
        }
    }

}