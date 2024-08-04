package com.flight.movie.infra.master.ui.personal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.Countries
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.databinding.ActivityLanguageBinding
import com.flight.movie.infra.master.money.InstallManager
import com.flight.movie.infra.master.money.Money
import com.flight.movie.infra.master.ui.EXTRA_COLD_START
import com.flight.movie.infra.master.ui.start.GuideActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LanguageActivity : AppCompatActivity() {

    private val dataFlow = MutableStateFlow<List<Countries>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val coldStart = intent.getBooleanExtra(EXTRA_COLD_START, false)
        binding.list.layoutManager = LinearLayoutManager(this)
        val languageAdapter = LanguageAdapter()

        languageAdapter.setOnItemClickListener { adapter, _, position ->
            val oldIndex = languageAdapter.checkedLanguage
            languageAdapter.checkedLanguage = position
            if (oldIndex != -1) {
                adapter.notifyItemChanged(oldIndex)
            }
            adapter.notifyItemChanged(position)
        }
        binding.list.adapter = languageAdapter

        val click = Firebase.remoteConfig.getBoolean("ClickRate")
        binding.adView.setCanClickAd(click)

        lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) {
                val countries = mutableListOf<Countries>()
                val json = assets.open("countries.json").reader().readText()
                val obj = JSONObject(json)
                val array = obj.getJSONArray("countries")
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    countries.add(
                        Countries(
                            item.getString("name"),
                            item.getString("alpha-2")
                        )
                    )
                }
                countries
            }
            Log.d("LanguageActivity", "onCreate: data = $data")
            dataFlow.update {
                data
            }
        }

        lifecycleScope.launch {
            dataFlow.collect {
                Log.d("LanguageActivity", "onCreate:   collect = ${it.size}")
                languageAdapter.submitList(it)
            }
        }

        if (!coldStart) {
            binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back__white_24)
            binding.toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        if (InstallManager.getRunB()) {
            Money.languageNativeLoader.refreshAd(this) {
                binding.adView.isVisible = true
                binding.adView.setNativeAd(it)
            }
        }

        binding.done.setOnClickListener {
            languageAdapter.checkedCountry()?.let {
                DataClient.defLanguage = it.code
                DataClient.defLanguageName.update { _ ->
                    it.name
                }
            }
            //jump guild or finish
            if (coldStart) {
                startActivity(Intent(this, GuideActivity::class.java))
            }
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Money.languageNativeLoader.destroy()
    }
}