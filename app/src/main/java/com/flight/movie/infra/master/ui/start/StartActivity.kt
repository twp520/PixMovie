package com.flight.movie.infra.master.ui.start

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.money.ActivityUtil
import com.flight.movie.infra.master.money.InstallManager
import com.flight.movie.infra.master.money.Money
import com.flight.movie.infra.master.money.ShareHelper
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class StartActivity : AppCompatActivity() {

    private var clickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        Money.prepareLoadSplash(this)
        ShareHelper.forceClicked()
        lifecycleScope.launch {
            try {
                val load = withContext(Dispatchers.Default) {
                    val timeout = Firebase.remoteConfig.getLong("timeSplash")
                    Log.d("StartActivity", "onCreate: timeout=$timeout")
                    return@withContext withTimeout(15000) {
                        delay(5000)
                        return@withTimeout if (Money.splashInterLoader.isReady()) {
                            true
                        } else {
                            while (!Money.splashInterLoader.isReady()) {
                                delay(1000)
                            }
                            true
                        }
                    }
                }
                if (load) {
                    Money.splashInterLoader.show(this@StartActivity,true) {
                        ShareHelper.forceClicked()
                        ActivityUtil.startToMain(this@StartActivity)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                //time out
                ShareHelper.forceClicked()
                ActivityUtil.startToMain(this@StartActivity)
            }
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("StartActivity", "handleOnBackPressed: ")
            }
        })

        findViewById<View>(R.id.start_icon).setOnClickListener {
            clickCount++
            if (clickCount == 10) {
                Log.d("StartActivity", "onCreate: setB")
                InstallManager.setRunB()
            }
        }
    }
}