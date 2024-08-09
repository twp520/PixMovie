package com.flight.movie.infra.master.money

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

/**
 * create by colin
 * 2024/8/1
 */
object AnalysisUtils {

    const val FROM_SPLASH_INTER = "splash_inter"
    const val FROM_GUIDE_INTER = "guide_inter"
    const val FROM_ENTER_DETAIL_MOVIE_INTER = "enter_movie_inter"
    const val FROM_ENTER_DETAIL_TV_INTER = "enter_tv_inter"
    const val FROM_ENTER_DETAIL_PEOPLE_INTER = "enter_people_inter"
    const val FROM_ENTER_DETAIL_DISCOVER_INTER = "discover_enter_inter"
    const val FROM_BACK_INTER = "back_inter"
    const val FROM_PLAY_INTER = "enter_play_inter"

    const val FROM_LANGUAGE_NATIVE = "language_native"
    const val FROM_GUIDE_NATIVE = "guide_native"
    const val FROM_HOME_CATEGORY_MOVIE_NATIVE = "home_movie_native"
    const val FROM_HOME_CATEGORY_TV_NATIVE = "home_tv_native"
    const val FROM_HOME_CATEGORY_PEOPLE_NATIVE = "home_people_native"
    const val FROM_DISCOVER_MOVIE_NATIVE = "discover_movie_native"
    const val FROM_DISCOVER_TV_NATIVE = "discover_tv_native"
    const val FROM_HOME_MORE_NATIVE = "home_more_native"
    const val FROM_DETAIL_MOVIE_NATIVE = "detail_movie_native"
    const val FROM_DETAIL_TV_NATIVE = "detail_tv_native"
    const val FROM_DETAIL_PEOPLE_NATIVE = "detail_people_native"
    const val FROM_TV_SERIES_NATIVE = "tv_series_native"
    const val FROM_PLAYER_NATIVE = "player_native"

    const val TYPE_NATIVE = "native"
    const val TYPE_INTER = "inter"

    fun logEvent(event: String, args: Bundle = Bundle.EMPTY) {
        val params = Bundle()
        params.putBoolean("isRunB", InstallManager.getRunB())
        params.putAll(args)
        Firebase.analytics.logEvent(event, params)
        // Log.d("AnalysisUtils", "logEvent: $event")
    }

    fun logAdImpressionEvent(from: String, type: String) {
        val eventName = if (type == TYPE_NATIVE) "ad_native_show" else "ad_inter_show"
        val params = bundleOf()
        params.putString("placements", from)
        logEvent(eventName, params)
    }

    fun logAdClickedEvent(from: String, type: String) {
        val eventName = if (type == TYPE_NATIVE) "ad_native_click" else "ad_inter_click"
        val params = bundleOf()
        params.putString("placements", from)
        logEvent(eventName, params)
    }

}