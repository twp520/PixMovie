package com.flight.movie.infra.master.money

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.flight.movie.infra.master.MyApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import java.text.SimpleDateFormat
import java.util.Date


object ShareHelper {

    private const val TAG = "ShareHelper"

    private val edit by lazy {
        MyApp.instance.getSharedPreferences("appData", Context.MODE_PRIVATE)
    }

    private const val keyEnable = "keyEnable"
    private const val keyInterClickCount = "keyInterClickCount"
    private const val keyNativeClickCount = "keyInterClickCount"
    private const val keyClickDate = "keyClickDate"

    private var userActivity = 10

    fun setEnable(enable: Boolean) {
        edit.edit().putBoolean(keyEnable, enable).apply()
    }

    fun getEnable(): Boolean {
        return edit.getBoolean(keyEnable, false)
    }

    fun hasEnable(): Boolean {
        return edit.contains(keyEnable)
    }

    fun initClickCount() {
        val clickData = edit.getString(keyClickDate, "")
        val now = SimpleDateFormat.getDateInstance().format(Date(System.currentTimeMillis()))
        Log.d(TAG, "initClickCount: now = $now , clickData = $now")
        if (!TextUtils.equals(now, clickData)) {
            //new day
            edit.edit().putString(keyClickDate, now).apply()
            edit.edit().remove(keyInterClickCount).remove(keyNativeClickCount).apply()
        }
    }

    fun onInterClicked() {
        val old = edit.getInt(keyInterClickCount, 0)
        edit.edit().putInt(keyInterClickCount, old + 1).apply()
    }

    // fun onNativeClicked() {
    //     val old = edit.getInt(keyNativeClickCount, 0)
    //     edit.edit().putInt(keyNativeClickCount, old + 1).apply()
    // }

    fun isInterMaxLimit(): Boolean {
        return edit.getInt(
            keyInterClickCount, 0
        ) >= Firebase.remoteConfig.getLong("interClickLimit")
    }

    fun userActivityClicked() {
        userActivity++
    }

    fun resetUserActivityClicked() {
        userActivity = 0
    }

    fun isUserClickAllow(): Boolean {
        Log.d(TAG, "isUserClickAllow: userActivity = $userActivity")
        return userActivity >= Firebase.remoteConfig.getLong("userActivity")
    }

    fun forceClicked() {
        val count = Firebase.remoteConfig.getLong("userActivity").toInt()
        userActivity = count + 5
    }
}