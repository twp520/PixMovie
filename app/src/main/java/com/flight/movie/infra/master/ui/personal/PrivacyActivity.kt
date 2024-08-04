package com.flight.movie.infra.master.ui.personal

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.databinding.ActivityPrivacyBinding

class PrivacyActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.webview.settings.apply {
            javaScriptEnabled = true
        }
        val filePath = "file:///android_asset/privacy_policy.html"
        binding.webview.loadUrl(filePath)
    }
}