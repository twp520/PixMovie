package com.flight.movie.infra.master.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.databinding.ActivityPlayBinding
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.money.InterLoader
import com.flight.movie.infra.master.money.NativeLoader
import com.flight.movie.infra.master.money.ShareHelper
import com.flight.movie.infra.master.ui.EXTRA_FILM
import com.flight.movie.infra.master.ui.detail.params.PlayerParams
import com.flight.movie.infra.master.ui.detail.vm.PlayerViewModel
import com.flight.movie.infra.master.ui.formatVote
import com.flight.movie.infra.master.ui.go2market
import com.flight.movie.infra.master.ui.jump2Browser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlayActivity : AppCompatActivity() {

    private val TAG = "PlayActivity"

    private val viewModel by viewModels<PlayerViewModel>()
    private lateinit var binding: ActivityPlayBinding
    private var adLoader: NativeLoader? = null
    private lateinit var interLoader: InterLoader
    private var isFirst = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ShareHelper.userActivityClicked()
        val playerParams = intent.getParcelableExtra<PlayerParams>(EXTRA_FILM) ?: return
        val playUrl = viewModel.getPlayUrl(playerParams)
        Log.d(TAG, "onCreate: $playUrl")
        binding.title.text = playerParams.filmName
        binding.seEp.isVisible = playerParams.filmType == DataClient.TYPE_TV
        binding.seEp.text = getString(R.string.s_e, playerParams.sNumber, playerParams.eNumber)
        binding.date.text = playerParams.date
        binding.vote.text = formatVote(playerParams.vote)
        Glide.with(this)
            .load(DataClient.getImageUrl(playerParams.cover))
            .into(binding.playCover)
        adLoader = NativeLoader(
            getString(R.string.native_test),
            lifecycleScope,
            AnalysisUtils.FROM_PLAYER_NATIVE
        )
        interLoader = InterLoader(
            getString(R.string.inter_test), lifecycleScope,
            AnalysisUtils.FROM_BACK_INTER
        )
        interLoader.load(this)

        binding.toolbar.setNavigationOnClickListener {
            ShareHelper.userActivityClicked()
            onBackPressedDispatcher.onBackPressed()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ShareHelper.userActivityClicked()
                interLoader.show(this@PlayActivity) {
                    finish()
                }
            }
        })
        binding.buttonRateUs.setOnClickListener {
            //go to rate
            ShareHelper.userActivityClicked()
            go2market(this)
        }

        binding.buttonLines.setOnClickListener {
            ShareHelper.userActivityClicked()
            //show lines dialog
            showLinesDialog()
        }

        binding.playIcon.setOnClickListener {
            ShareHelper.forceClicked()
            showTipsDialog(playUrl)
        }

        lifecycleScope.launch {
            viewModel.linesData.collectLatest {
                binding.buttonLines.text = getString(R.string.string_lines, (it + 1))
                binding.loadingLayout.isVisible = true
                loadAd()
            }
        }

    }

    private fun loadAd() {
        adLoader?.refreshAd(this) {
            binding.loadingLayout.isVisible = false
            binding.playAdView.setNativeAd(it)
            if (!isFirst) {
                isFirst = false
                interLoader.show(this) {

                }
            }
        }

        lifecycleScope.launch(Dispatchers.Default) {
            delay(15000)
            withContext(Dispatchers.Main) {
                binding.loadingLayout.isVisible = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adLoader?.destroy()
    }

    private fun showTipsDialog(url: String) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.play_message))
            .setPositiveButton(R.string.done) { dialog, _ ->
                dialog.dismiss()
                jump2Browser(this, url)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun showLinesDialog() {
        LinesListDialogFragment.newInstance(2)
            .apply {
                itemClickListener = {
                    viewModel.chooseLines(it)
                }
            }
            .show(supportFragmentManager, "LinesListDialogFragment")
    }

}