package com.flight.movie.infra.master.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.FilmImageItem
import com.flight.movie.infra.master.data.FilmItem
import com.flight.movie.infra.master.data.TvEpDetail
import com.flight.movie.infra.master.data.TvEpisodes
import com.flight.movie.infra.master.databinding.ActivityTvDetailBinding
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.money.InstallManager
import com.flight.movie.infra.master.money.InterLoader
import com.flight.movie.infra.master.money.NativeLoader
import com.flight.movie.infra.master.money.ShareHelper
import com.flight.movie.infra.master.ui.COL_COUNT
import com.flight.movie.infra.master.ui.EXTRA_EP
import com.flight.movie.infra.master.ui.EXTRA_FILM
import com.flight.movie.infra.master.ui.TAG
import com.flight.movie.infra.master.ui.base.GridItemDecoration
import com.flight.movie.infra.master.ui.detail.adapter.CastListAdapter
import com.flight.movie.infra.master.ui.detail.adapter.ImagePageAdapter
import com.flight.movie.infra.master.ui.detail.adapter.TvEpisodeAdapter
import com.flight.movie.infra.master.ui.detail.params.PlayerParams
import com.flight.movie.infra.master.ui.detail.params.TvEpDetailParams
import com.flight.movie.infra.master.ui.detail.vm.TvDetailViewModel
import com.flight.movie.infra.master.ui.formatVote
import com.flight.movie.infra.master.ui.home.FavoriteViewModel
import com.flight.movie.infra.master.ui.home.adapter.FilmAdapter
import com.flight.movie.infra.master.ui.startDetail
import com.lm.piccolo.Piccolo
import com.lm.piccolo.view.ConductorForView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TvEpDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTvDetailBinding

    private val viewModel by viewModels<TvDetailViewModel>()
    private val favoriteViewModel by viewModels<FavoriteViewModel>()
    private val loadingViews = mutableListOf<ConductorForView>()
    private lateinit var castAdapter: CastListAdapter
    private lateinit var nativeLoader: NativeLoader
    private lateinit var interEnterDetailLoader: InterLoader
    private lateinit var interBackDetailLoader: InterLoader
    private lateinit var interPlayLoader: InterLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ShareHelper.userActivityClicked()
        castAdapter = CastListAdapter(viewModel.viewModelScope, AnalysisUtils.FROM_DETAIL_TV_NATIVE)
        nativeLoader = NativeLoader(
            getString(R.string.native_test),
            lifecycleScope,
            AnalysisUtils.FROM_DETAIL_TV_NATIVE, true
        )
        interEnterDetailLoader = InterLoader(
            getString(R.string.inter_test), lifecycleScope,
            AnalysisUtils.FROM_ENTER_DETAIL_TV_INTER
        )
        interBackDetailLoader = InterLoader(
            getString(R.string.inter_test), lifecycleScope,
            AnalysisUtils.FROM_BACK_INTER
        )
        interPlayLoader = InterLoader(
            getString(R.string.inter_test), lifecycleScope,
            AnalysisUtils.FROM_PLAY_INTER
        )
        interEnterDetailLoader.load(this)
        interBackDetailLoader.load(this)
        interPlayLoader.load(this)

        binding.appbarContent.toolbar.setNavigationOnClickListener {
            ShareHelper.userActivityClicked()
            onBackPressedDispatcher.onBackPressed()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ShareHelper.userActivityClicked()
                interPlayLoader.show(this@TvEpDetailActivity) {
                    finish()
                }
            }
        })
        val detailParams = intent.getParcelableExtra<TvEpDetailParams>(EXTRA_EP) ?: return
        binding.detail.detailEpTag.isVisible = true
        binding.detail.episodesLoading.isVisible = true
        binding.detail.detailEpisodes.isVisible = true
        binding.appbarContent.fab.isVisible = InstallManager.getRunB()

        binding.appbarContent.toolbarLayout.title = detailParams.name + "S${detailParams.seNumber}"
        binding.detail.detailCountry.text = detailParams.country
        binding.appbarContent.detailGenres.text = detailParams.tags

        val isFavorite = favoriteViewModel.isFavorite(detailParams)

        binding.detail.buttonFav.setOnClickListener { view ->
            ShareHelper.userActivityClicked()
            if (view.isSelected) {
                favoriteViewModel.unFavoriteTv(detailParams)
            } else {
                favoriteViewModel.favoriteTv(detailParams)
            }
        }

        binding.detail.detailCast.layoutManager = QuickGridLayoutManager(this, COL_COUNT)
        binding.detail.detailCast.addItemDecoration(
            GridItemDecoration(
                resources.getDimensionPixelSize(
                    R.dimen.activity_horizontal_margin
                ), COL_COUNT, true
            )
        )
        binding.detail.detailCast.adapter = castAdapter


        loadingViews.add(Piccolo.createForView(binding.detail.overviewLoading))
        loadingViews.add(Piccolo.createForView(binding.detail.countryLoading))
        loadingViews.add(Piccolo.createForView(binding.detail.directorLoading))
        loadingViews.add(Piccolo.createForView(binding.detail.recommendLoading))
        loadingViews.add(Piccolo.createForView(binding.detail.statusLoading))
        loadingViews.add(Piccolo.createForView(binding.detail.episodesLoading))

        binding.detail.detailRecommend.layoutManager = GridLayoutManager(this, COL_COUNT)
        binding.detail.detailRecommend.addItemDecoration(
            GridItemDecoration(
                resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin),
                COL_COUNT,
                true
            )
        )

        binding.detail.detailEpisodes.layoutManager = GridLayoutManager(this, 7)
        viewModel.requestTvEpActData(detailParams.tvId, detailParams.seNumber)

        lifecycleScope.launch {
            viewModel.tvEpDetail.collect {
                bindDetail(it, detailParams)
            }
        }
        lifecycleScope.launch {
            viewModel.tvEpRecommend.collect {
                bindRecommend(it)
            }
        }
        lifecycleScope.launch {
            viewModel.tvEpImages.collect {
                bindImages(it)
            }
        }

        lifecycleScope.launch {
            isFavorite.collectLatest {
                binding.detail.buttonFav.isSelected = it
            }
        }

        lifecycleScope.launch {
            viewModel.castData.collect {
                castAdapter.submitList(it)
            }
        }
        if (InstallManager.getRunB()) {
            nativeLoader.refreshAd(this) {
                Log.d(TAG, "onCreate: refreshAd !!!")
                binding.detail.detailNativeAdView.isVisible = true
                binding.detail.detailNativeAdView.setNativeAd(it)
            }
        } else {
            binding.detail.detailNativeAdView.isVisible = false
        }

    }

    private fun bindDetail(result: Result<TvEpDetail>?, detailParams: TvEpDetailParams) {
        if (result == null) {
            loadingViews.forEach {
                it.visible(true).play()
            }
        } else {
            result.getOrNull()?.let {
                loadingViews.forEach { view ->
                    view.visible(false).play()
                }
                //init ep list
                val epAdapter = TvEpisodeAdapter(it.episodes)
                binding.detail.detailEpisodes.adapter = epAdapter
                epAdapter.setOnItemClickListener { _, _, position ->
                    ShareHelper.userActivityClicked()
                    epAdapter.getItem(position)?.let { ep ->
                        val oldPosition = epAdapter.checkedPosition
                        epAdapter.checkedPosition = position
                        epAdapter.notifyItemChanged(oldPosition)
                        epAdapter.notifyItemChanged(position)
                        bindEpisode(ep)
                        if (InstallManager.getRunB()) {
                            interEnterDetailLoader.show(this) {

                            }
                        }
                    }
                }
                it.episodes.firstOrNull()?.let { ep ->
                    ShareHelper.userActivityClicked()
                    bindEpisode(ep)
                }

                binding.appbarContent.fab.setOnClickListener {
                    ShareHelper.userActivityClicked()
                    val episode =
                        epAdapter.getItem(epAdapter.checkedPosition) ?: return@setOnClickListener
                    interPlayLoader.show(this) {
                        startActivity(Intent(this, PlayActivity::class.java).apply {
                            putExtra(
                                EXTRA_FILM, PlayerParams(
                                    detailParams.tvId, DataClient.TYPE_TV,
                                    detailParams.seNumber,
                                    episode.epNumber,
                                    detailParams.name,
                                    episode.vote,
                                    episode.airDate,
                                    episode.poster
                                )
                            )
                        })
                    }

                }
            }
        }
    }

    private fun bindEpisode(episodes: TvEpisodes) {
        binding.appbarContent.detailRate.text = formatVote(episodes.vote)
        binding.detail.detailDirector.text = episodes.getDirector()
        binding.detail.detailStatus.text = episodes.epType
        binding.appbarContent.detailDuration.text = getString(R.string.min_s, episodes.runtime)
        binding.appbarContent.detailData.text = episodes.airDate
        binding.detail.detailOverview.text = episodes.overview
        Glide.with(this)
            .load(DataClient.getImageUrl(episodes.poster))
            .placeholder(R.drawable.shape_placeholder)
            .error(R.drawable.baseline_broken_image_24)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.appbarContent.poster)
    }

    private fun bindRecommend(result: Result<List<FilmItem>>) {
        val list = result.getOrNull()
        if (list != null) {
            val recommend = if (list.size > 6) {
                list.subList(0, 6)
            } else {
                list
            }
            val recommendAdapter = FilmAdapter(recommend, DataClient.TYPE_TV)
            binding.detail.detailRecommend.adapter = recommendAdapter
            recommendAdapter.setOnItemClickListener { _, _, position ->
                ShareHelper.userActivityClicked()
                recommendAdapter.getItem(position)?.let {
                    if (InstallManager.getRunB()) {
                        interEnterDetailLoader.show(this) {
                            startDetail(this, it.mediaType ?: DataClient.TYPE_TV, it)
                        }
                    } else {
                        startDetail(this, it.mediaType ?: DataClient.TYPE_TV, it)
                    }
                }
            }
        } else {
            binding.detail.recommendLoading.isVisible = false
            binding.detail.detailRecommend.isVisible = false
            binding.detail.detailTagRecommend.isVisible = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindImages(result: Result<List<FilmImageItem>>) {
        val list = result.getOrNull()
        if (list.isNullOrEmpty()) {
            binding.detail.posterLoading.isVisible = false
            binding.detail.posterTag.isVisible = false
            binding.detail.detailPosters.isVisible = false
            binding.detail.detailPostersIndication.isVisible = false
        } else {
            binding.detail.posterLoading.isVisible = true
            binding.detail.posterTag.isVisible = true
            binding.detail.detailPosters.isVisible = true
            binding.detail.detailPostersIndication.isVisible = true
            val pageAdapter = ImagePageAdapter(list)
            binding.detail.detailPosters.adapter = pageAdapter
            binding.detail.detailPosters.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    ShareHelper.userActivityClicked()
                    binding.detail.detailPostersIndication.text =
                        "${position + 1}/${pageAdapter.itemCount}"
                }
            })
            binding.detail.detailPostersIndication.text =
                "1/${pageAdapter.itemCount}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeLoader.destroy()
    }
}