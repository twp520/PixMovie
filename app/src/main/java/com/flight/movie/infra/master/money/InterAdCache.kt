package com.flight.movie.infra.master.money

import android.content.Context
import com.flight.movie.infra.master.MyApp
import com.flight.movie.infra.master.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * create by colin
 * 2024/8/11
 */
object InterAdCache {

    private const val cacheSize = 2
    private val cacheList = mutableListOf<InterstitialAd>()

    private val loadListener = object : InterstitialAdLoadCallback() {

        override fun onAdFailedToLoad(adError: LoadAdError) {

        }

        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            cacheList.add(interstitialAd)
            if (cacheList.size < cacheSize) {
                load(MyApp.instance.applicationContext)
            }
        }
    }

    fun load(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            context.getString(R.string.inter_test),
            adRequest,
            loadListener
        )
    }

    fun peekNativeAd(context: Context): InterstitialAd? {
        val cache = cacheList.removeFirstOrNull()
        if (cacheList.size < cacheSize) {
            load(context)
        }
        return cache
    }

    fun destroy() {
        cacheList.clear()
    }

}