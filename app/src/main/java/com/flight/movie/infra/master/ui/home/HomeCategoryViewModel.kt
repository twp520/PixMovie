package com.flight.movie.infra.master.ui.home

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.MovieRepository
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.money.InstallManager
import com.flight.movie.infra.master.money.InterLoader
import com.flight.movie.infra.master.money.NativeLoader
import com.flight.movie.infra.master.ui.TAG
import com.flight.movie.infra.master.ui.state.HomeCategoryUiState
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * create by colin
 * 2024/7/2
 */
class HomeCategoryViewModel : ViewModel() {

    // private val TAG = "HomeCategoryViewModel"
    private val movieRepository = MovieRepository(DataClient.service)

    private val _categoryData = MutableStateFlow<Result<List<HomeCategoryUiState>>?>(null)
    val categoryData = _categoryData.asStateFlow()

    val nativeAdList = MutableStateFlow<Pair<Int, NativeAd>?>(null)

    private var interLoader: InterLoader? = null

    fun requestCategory(context: Context, type: String) {
        viewModelScope.launch {
            val result = mutableListOf<HomeCategoryUiState>()
            movieRepository.requestTrendingCategory(type).getOrNull()?.let {
                result.add(
                    HomeCategoryUiState(
                        type,
                        context.getString(R.string.trending),
                        it.results,
                        DataClient.CATEGORY_TRENDING
                    )
                )
            }

            movieRepository.requestHomeCategoryList(type, DataClient.CATEGORY_TOP_RATED).getOrNull()
                ?.let {
                    // Log.d(TAG, "requestCategory: CATEGORY_TOP_RATED = ${it.results}")
                    result.add(
                        HomeCategoryUiState(
                            type,
                            context.getString(R.string.top_rate),
                            it.results,
                            DataClient.CATEGORY_TOP_RATED
                        )
                    )
                }

            val nowPlaying =
                if (type == DataClient.TYPE_MOVIE) DataClient.CATEGORY_NOW_PLAYING_MOVIE else
                    DataClient.CATEGORY_NOW_PLAYING_TV
            movieRepository.requestHomeCategoryList(type, nowPlaying).getOrNull()?.let {
                // Log.d(TAG, "requestCategory: nowPlaying = ${it.results}")
                result.add(
                    HomeCategoryUiState(
                        type,
                        context.getString(R.string.now_playing),
                        it.results,
                        nowPlaying
                    )
                )
            }

            movieRepository.requestHomeCategoryList(type, DataClient.CATEGORY_POPULAR).getOrNull()
                ?.let {
                    // Log.d(TAG, "requestCategory: CATEGORY_POPULAR = ${it.results}")
                    result.add(
                        HomeCategoryUiState(
                            type,
                            context.getString(R.string.popular),
                            it.results,
                            DataClient.CATEGORY_POPULAR
                        )
                    )
                }

            _categoryData.update {
                if (result.isEmpty()) {
                    Result.failure(NullPointerException("No data"))
                } else {
                    Result.success(result)
                }
            }
            if (InstallManager.getRunB()) {
                val from = if (type == DataClient.TYPE_MOVIE)
                    AnalysisUtils.FROM_HOME_CATEGORY_MOVIE_NATIVE else
                    AnalysisUtils.FROM_HOME_CATEGORY_TV_NATIVE
                loadHomeCategoryAd(context, result.size, from)
            }
        }
    }

    fun retryCategory(context: Context, type: String) {
        _categoryData.update {
            null
        }
        requestCategory(context, type)
    }

    private fun loadHomeCategoryAd(context: Context, size: Int, from: String) {
        Log.d(TAG, "loadHomeCategoryAd: size = $size")
        for (i in 0 until size) {
            val nativeLoader =
                NativeLoader(
                    context.getString(R.string.native_test), viewModelScope, from,
                    true
                )
            nativeLoader.refreshAd(context) { ad ->
                nativeAdList.update {
                    Pair(i, ad)
                }
            }
        }
    }

    fun initInterAD(context: Activity, id: String, from: String) {
        interLoader = InterLoader(id, viewModelScope, from)
        if (InstallManager.getRunB()) {
            interLoader?.load(context)
        }
    }

    fun showInterAD(activity: Activity, runnable: Runnable) {
        interLoader?.show(activity, runnable)
    }

    override fun onCleared() {
        super.onCleared()
        interLoader?.destroy()
    }

}