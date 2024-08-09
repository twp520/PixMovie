package com.flight.movie.infra.master.ui.start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.flight.movie.infra.master.MainActivity
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.databinding.ActivityGuideBinding
import com.flight.movie.infra.master.money.InstallManager
import com.flight.movie.infra.master.money.Money
import com.flight.movie.infra.master.money.ShareHelper
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GuideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ShareHelper.forceClicked()
        val covers = arrayOf(R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3)
        val titles = resources.getStringArray(R.array.guide_tip)
        val currentIndex = MutableStateFlow(0)
        binding.btnNext.setOnClickListener {
            if (currentIndex.value < titles.size - 1) {
                currentIndex.update {
                    it + 1
                }
            } else {
                Money.guideInterLoader.show(this) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            currentIndex.collectLatest {
                binding.guideVp.setImageResource(covers[it])
                binding.tvTip.text = titles[it]
                binding.introduction.forEachIndexed { index, view ->
                    view.isSelected = index == it
                }
                if (it == titles.size - 1) {
                    binding.btnNext.text = getString(R.string.get_start)
                }
            }
        }

        val click = Firebase.remoteConfig.getBoolean("ClickRate")
        binding.adView.setCanClickAd(click)

        if (InstallManager.getRunB()) {
            Money.guideNativeLoader.refreshAd(this) {
                binding.adView.isVisible = true
                binding.adView.setNativeAd(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Money.guideNativeLoader.destroy()
    }
}