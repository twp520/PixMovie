package com.flight.movie.infra.master.money

import android.app.Activity
import android.content.Context
import android.util.Log
import com.flight.movie.infra.master.MyApp
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * create by colin
 * 2024/7/18
 */
class InterLoader(
    private val id: String,
    private val scope: CoroutineScope,
    private val from: String
) {

    private var mInterstitialAd: InterstitialAd? = null
    private var retryCount = 0

    private var isLoading = false
    private var delayLoadJob: Job? = null

    fun load(context: Context) {
        if (ShareHelper.isInterMaxLimit()) {
            Log.d("InterLoader", "load: isInterMaxLimit -> return")
            AnalysisUtils.logEvent("interMaxLimit")
            return
        }
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            id,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    isLoading = false
                    //delay 30s retry
                    delayLoad()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    isLoading = false
                    mInterstitialAd = interstitialAd
                }
            })
    }

    fun show(activity: Activity, isRunB: Boolean = InstallManager.getRunB(), runnable: Runnable) {
        Log.d("InterLoader", "show:  mInterstitialAd:${mInterstitialAd != null}")
        if (!isRunB) {
            runnable.run()
            return
        }
        val ad = mInterstitialAd
        if (ad == null) {
            runnable.run()
            if (!isLoading) {
                delayLoad(100, true)
            }
            return
        }
        Log.d("InterLoader", "show -> isUserClickAllow  ${ShareHelper.isUserClickAllow()}")
        if (!ShareHelper.isUserClickAllow()) {
            runnable.run()
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                ShareHelper.onInterClicked()
                AnalysisUtils.logAdClickedEvent(from, AnalysisUtils.TYPE_INTER)
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                mInterstitialAd = null
                runnable.run()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                //放弃这个广告,延迟30s重新请求
                mInterstitialAd = null
                delayLoad()
                runnable.run()
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                AnalysisUtils.logAdImpressionEvent(from, AnalysisUtils.TYPE_INTER)
                ShareHelper.resetUserActivityClicked()
                delayLoad(100, true)
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.

            }
        }
        ad.show(activity)
    }

    fun isReady(): Boolean {
        return mInterstitialAd != null
    }

    private fun delayLoad(delay: Long = 10000L, force: Boolean = false) {
        delayLoadJob?.cancel()
        retryCount++
        if (retryCount > 10)
            retryCount = 1
        val time = if (force) {
            delay
        } else {
            delay * retryCount
        }
        delayLoadJob = scope.launch {
            delay(time)
            load(MyApp.instance.applicationContext)
        }
    }

    fun destroy() {
        mInterstitialAd = null
        delayLoadJob?.cancel()
        delayLoadJob = null
    }

    fun shouldReLoad(): Boolean {
        return !isLoading && mInterstitialAd == null
    }
}