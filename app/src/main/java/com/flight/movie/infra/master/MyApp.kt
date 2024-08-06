package com.flight.movie.infra.master

import android.app.Application
import android.util.Log
import com.flight.movie.infra.master.money.ActivityUtil
import com.flight.movie.infra.master.money.Money
import com.flight.movie.infra.master.money.NativeAdCache
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

/**
 * create by colin
 * 2024/7/11
 */
class MyApp : Application() {

    companion object {
        lateinit var instance: MyApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ActivityUtil.initApp(this)
        Firebase.initialize(this)
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            Log.d("MyApp", "App onCreate: firebase fetch : ${it.isSuccessful}")
        }
        Money.init(applicationContext)
        NativeAdCache.init(applicationContext)
    }
}