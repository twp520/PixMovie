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
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.FilmImageItem
import com.flight.movie.infra.master.data.FilmItem
import com.flight.movie.infra.master.data.MovieDetail
import com.flight.movie.infra.master.databinding.ActivityDetailBinding
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.money.InstallManager
import com.flight.movie.infra.master.money.InterLoader
import com.flight.movie.infra.master.money.NativeLoader
import com.flight.movie.infra.master.money.ShareHelper
import com.flight.movie.infra.master.ui.COL_COUNT
import com.flight.movie.infra.master.ui.EXTRA_FILM
import com.flight.movie.infra.master.ui.TAG
import com.flight.movie.infra.master.ui.base.GridItemDecoration
import com.flight.movie.infra.master.ui.detail.adapter.CastListAdapter
import com.flight.movie.infra.master.ui.detail.adapter.ImagePageAdapter
import com.flight.movie.infra.master.ui.detail.params.PlayerParams
import com.flight.movie.infra.master.ui.detail.vm.MovieDetailViewModel
import com.flight.movie.infra.master.ui.formatVote
import com.flight.movie.infra.master.ui.fromFilm
import com.flight.movie.infra.master.ui.home.FavoriteViewModel
import com.flight.movie.infra.master.ui.home.adapter.FilmAdapter
import com.flight.movie.infra.master.ui.startDetail
import com.lm.piccolo.Piccolo
import com.lm.piccolo.view.ConductorForView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<MovieDetailViewModel>()
    private val favoriteViewModel by viewModels<FavoriteViewModel>()
    private val loadingViews = mutableListOf<ConductorForView>()
    private val recommendAdapter = FilmAdapter(emptyList(), DataClient.TYPE_MOVIE)
    private lateinit var castAdapter: CastListAdapter
    private lateinit var nativeLoader: NativeLoader
    private lateinit var interEnterDetailLoader: InterLoader
    private lateinit var interBackDetailLoader: InterLoader
    private lateinit var interPlayLoader: InterLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ShareHelper.userActivityClicked()
        castAdapter = CastListAdapter(
            viewModel.viewModelScope,
            AnalysisUtils.FROM_DETAIL_MOVIE_NATIVE
        )
        nativeLoader = NativeLoader(
            getString(R.string.native_test),
            lifecycleScope,
            AnalysisUtils.FROM_DETAIL_MOVIE_NATIVE, true
        )
        interEnterDetailLoader = InterLoader(
            getString(R.string.inter_test),
            lifecycleScope,
            AnalysisUtils.FROM_ENTER_DETAIL_MOVIE_INTER
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

        binding.appbarContent.fab.isVisible = InstallManager.getRunB()
        binding.appbarContent.toolbar.setNavigationOnClickListener {
            ShareHelper.userActivityClicked()
            onBackPressedDispatcher.onBackPressed()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ShareHelper.userActivityClicked()
                interBackDetailLoader.show(this@MovieDetailActivity) {
                    finish()
                }
            }
        })

        val filmItem = intent.getParcelableExtra<FilmItem>(EXTRA_FILM) ?: return
        viewModel.requestMovieDetail(filmItem.id)
        viewModel.requestMovieImages(filmItem.id)

        binding.appbarContent.toolbarLayout.title = filmItem.displayName
        Glide.with(this).load(DataClient.getImageUrl(filmItem.poster))
            .placeholder(R.drawable.shape_placeholder).error(R.drawable.baseline_broken_image_24)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.appbarContent.poster)
        binding.appbarContent.detailRate.text = filmItem.vote.toString()
        val favoriteMovie = fromFilm(filmItem)

        val isFavorite = favoriteViewModel.isFavorite(favoriteMovie)

        binding.detail.buttonFav.setOnClickListener {
            ShareHelper.userActivityClicked()
            if (it.isSelected) {
                favoriteViewModel.unFavoriteFilm(favoriteMovie)
            } else {
                favoriteViewModel.favoriteFilm(favoriteMovie)
            }
        }

        loadingViews.add(Piccolo.createForView(binding.detail.overviewLoading))
        loadingViews.add(Piccolo.createForView(binding.detail.countryLoading))
        loadingViews.add(Piccolo.createForView(binding.detail.directorLoading))
        loadingViews.add(Piccolo.createForView(binding.detail.recommendLoading))
        loadingViews.add(Piccolo.createForView(binding.detail.statusLoading))

        binding.detail.detailRecommend.layoutManager = GridLayoutManager(this, COL_COUNT)
        binding.detail.detailRecommend.addItemDecoration(
            GridItemDecoration(
                resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin), COL_COUNT, true
            )
        )
        binding.detail.detailRecommend.adapter = recommendAdapter
        recommendAdapter.setOnItemClickListener { _, _, position ->
            ShareHelper.userActivityClicked()
            recommendAdapter.getItem(position)?.let {
                if (InstallManager.getRunB()) {
                    interEnterDetailLoader.show(this) {
                        startDetail(this, it.mediaType ?: DataClient.TYPE_MOVIE, it)
                    }
                } else {
                    startDetail(this, it.mediaType ?: DataClient.TYPE_MOVIE, it)
                }
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

        lifecycleScope.launch {
            viewModel.movieDetail.collect {
                bindDetail(it, filmItem.poster ?: "")
            }
        }

        lifecycleScope.launch {
            viewModel.movieImages.collect {
                bindImages(it)
            }
        }


        lifecycleScope.launch {
            isFavorite.collectLatest {
                Log.d("MovieDetailActivity", "onCreate: fav changed = $it ")
                binding.detail.buttonFav.isSelected = it
                binding.detail.buttonFav.text =
                    if (it) getString(R.string.remove_watch_list) else getString(R.string.add_to_watch_list)
            }
        }

        lifecycleScope.launch {
            viewModel.castData.collect {
                //cast
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
                OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    ShareHelper.userActivityClicked()
                    binding.detail.detailPostersIndication.text =
                        "${position + 1}/${pageAdapter.itemCount}"
                }
            })
            binding.detail.detailPostersIndication.text = "1/${pageAdapter.itemCount}"
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun bindDetail(result: Result<MovieDetail>?, cover: String) {
        if (result == null) {
            //loading state
            Log.d("MovieDetailActivity", "bindDetail: result is NULL")
            loadingViews.forEach {
                it.visible(true).play()
            }
        } else {
            result.getOrNull()?.let {
                binding.appbarContent.detailGenres.text = it.displayGenres
                binding.appbarContent.detailRate.text = formatVote(it.vote)
                binding.detail.detailCountry.text = it.displayCountry
                binding.detail.detailDirector.text = it.credits.getDirector
                binding.detail.detailStatus.text = it.status
                binding.appbarContent.detailDuration.text = getString(R.string.min_s, it.runtime)
                binding.appbarContent.detailData.text = it.releaseDate
                binding.detail.detailOverview.text = it.overview
                //recommend
                val recommend = withContext(Dispatchers.Default) {
                    if (it.recommendations.results.size > 6) {
                        it.recommendations.results.subList(0, 6)
                    } else {
                        it.recommendations.results
                    }
                }
                recommendAdapter.submitList(recommend)
                loadingViews.forEach { loading ->
                    loading.visible(false).play()
                }
                binding.appbarContent.fab.setOnClickListener { _ ->
                    ShareHelper.userActivityClicked()
                    interPlayLoader.show(this) {
                        startActivity(Intent(this, PlayActivity::class.java).apply {
                            putExtra(
                                EXTRA_FILM, PlayerParams(
                                    it.imdbId, DataClient.TYPE_MOVIE,
                                    filmName = it.title,
                                    vote = it.vote,
                                    date = it.releaseDate,
                                    cover = cover
                                )
                            )
                        })
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeLoader.destroy()
        loadingViews.clear()
    }
}