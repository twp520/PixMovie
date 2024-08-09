package com.flight.movie.infra.master.ui.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.QuickAdapterHelper
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.FilmItem
import com.flight.movie.infra.master.data.TVSeriesDetail
import com.flight.movie.infra.master.data.TvSeries
import com.flight.movie.infra.master.databinding.ActivityTvSeriesBinding
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.money.InterLoader
import com.flight.movie.infra.master.money.ShareHelper
import com.flight.movie.infra.master.ui.EXTRA_EP
import com.flight.movie.infra.master.ui.EXTRA_FILM
import com.flight.movie.infra.master.ui.base.QuickAdHeaderAdapter
import com.flight.movie.infra.master.ui.base.VerItemDecoration
import com.flight.movie.infra.master.ui.createFromTvSeriesDetail
import com.flight.movie.infra.master.ui.detail.adapter.TvSeriesListAdapter
import com.flight.movie.infra.master.ui.detail.vm.TvDetailViewModel
import com.lm.piccolo.Piccolo
import com.lm.piccolo.view.ConductorForAdapter
import kotlinx.coroutines.launch

class TvSeriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTvSeriesBinding
    private val viewModel by viewModels<TvDetailViewModel>()

    private lateinit var filmLoading: ConductorForAdapter
    private val tvSeriesListAdapter = TvSeriesListAdapter()
    private lateinit var adAdapter: QuickAdHeaderAdapter
    private val mAdapterHelper = QuickAdapterHelper.Builder(tvSeriesListAdapter).build()
    private lateinit var interEnterDetail: InterLoader
    private lateinit var interBack: InterLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ShareHelper.userActivityClicked()
        binding = ActivityTvSeriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adAdapter = QuickAdHeaderAdapter(
            viewModel.viewModelScope,
            AnalysisUtils.FROM_TV_SERIES_NATIVE
        )
        interEnterDetail = InterLoader(
            getString(R.string.inter_test), lifecycleScope,
            AnalysisUtils.FROM_ENTER_DETAIL_TV_INTER
        )
        interBack = InterLoader(
            getString(R.string.inter_test),
            lifecycleScope,
            AnalysisUtils.FROM_BACK_INTER
        )
        interEnterDetail.load(this)
        interBack.load(this)
        binding.tvSeContent.toolbar.setNavigationOnClickListener {
            ShareHelper.userActivityClicked()
            onBackPressedDispatcher.onBackPressed()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ShareHelper.userActivityClicked()
                interBack.show(this@TvSeriesActivity) {
                    finish()
                }
            }
        })
        val filmItem = intent.getParcelableExtra<FilmItem>(EXTRA_FILM) ?: return
        binding.tvSeContent.toolbarLayout.title = filmItem.displayName
        Glide.with(this)
            .load(DataClient.getImageUrl(filmItem.poster))
            .placeholder(R.drawable.shape_placeholder)
            .error(R.drawable.baseline_broken_image_24)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.tvSeContent.poster)

        filmLoading = Piccolo.createForList(binding.tvSeries)
        binding.tvSeries.layoutManager = LinearLayoutManager(this)
        binding.tvSeries.addItemDecoration(VerItemDecoration(resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)))
        filmLoading.items(
            intArrayOf(
                R.layout.item_tv_series,
            )
        ).visible(true).adapter(mAdapterHelper.adapter)
        mAdapterHelper.addBeforeAdapter(adAdapter)

        viewModel.requestTVSeries(filmItem.id)
        lifecycleScope.launch {
            viewModel.tvSeriesDetail.collect {
                bindTvSeries(it, filmItem)
            }
        }
    }

    private fun bindTvSeries(result: Result<TVSeriesDetail>?, filmItem: FilmItem) {
        if (result == null) {
            filmLoading.play()
        } else {
            result.getOrNull()?.let {
                filmLoading.visible(false).play()
                binding.tvSeContent.overview.text = it.overview
                binding.tvSeContent.tag.text = it.genres.joinToString(" / ") { genres ->
                    genres.name
                }
                tvSeriesListAdapter.submitList(it.seasons.filter { se -> se.seasonNumber > 0 })
                tvSeriesListAdapter.addOnItemChildClickListener(R.id.item_tv_series_btn) { _, _, position ->
                    ShareHelper.userActivityClicked()
                    tvSeriesListAdapter.getItem(position)?.let { se ->
                        interEnterDetail.show(this) {
                            go2TvEpDetail(it, se, filmItem)
                        }
                    }
                }
            }
        }
    }

    private fun go2TvEpDetail(
        it: TVSeriesDetail,
        se: TvSeries,
        filmItem: FilmItem
    ) {
        startActivity(Intent(this, TvEpDetailActivity::class.java).apply {
            putExtra(
                EXTRA_EP,
                createFromTvSeriesDetail(it, se.seasonNumber, filmItem.vote)
            )
        })
    }

}