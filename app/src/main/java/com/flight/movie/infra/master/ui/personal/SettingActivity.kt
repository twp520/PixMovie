package com.flight.movie.infra.master.ui.personal

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.databinding.ActivitySettingBinding
import com.flight.movie.infra.master.ui.shareApp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            DataClient.defLanguageName.collectLatest {
                binding.settingLanguageInfo.text = it
            }
        }
        try {
            val pInfo: PackageInfo =
                packageManager.getPackageInfo(packageName, 0)
            binding.settingVersion.text = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.settingLanguage.setOnClickListener {
            startActivity(Intent(this,LanguageActivity::class.java))
        }
        binding.settingPrivacy.setOnClickListener {
            startActivity(Intent(this, PrivacyActivity::class.java))
        }
        binding.settingShare.setOnClickListener {
            shareApp(this)
        }
        binding.settingFeedback.setOnClickListener {
            composeEmail()
        }
    }


    private fun composeEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only email apps handle this.
            putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback))
        }
        val email = Intent.createChooser(intent, null)
        startActivity(email)
    }
}