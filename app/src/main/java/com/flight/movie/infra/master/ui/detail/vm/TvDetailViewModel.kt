package com.flight.movie.infra.master.ui.detail.vm

import androidx.lifecycle.viewModelScope
import com.flight.movie.infra.master.data.FilmImageItem
import com.flight.movie.infra.master.data.FilmItem
import com.flight.movie.infra.master.data.TVSeriesDetail
import com.flight.movie.infra.master.data.TvEpDetail
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * create by colin
 * 2024/7/23
 */
class TvDetailViewModel : BaseDetailViewModel() {

    private val _tvSeriesDetail = MutableStateFlow<Result<TVSeriesDetail>?>(null)
    val tvSeriesDetail = _tvSeriesDetail.asStateFlow()

    private val _tvEpDetail = MutableStateFlow<Result<TvEpDetail>?>(null)
    val tvEpDetail = _tvEpDetail.asStateFlow()

    private val _tvEpRecommend = MutableStateFlow<Result<List<FilmItem>>>(
        Result.success(
            emptyList()
        )
    )
    val tvEpRecommend = _tvEpRecommend.asStateFlow()

    private val _tvEpImages = MutableStateFlow<Result<List<FilmImageItem>>>(
        Result.success(
            emptyList()
        )
    )
    val tvEpImages = _tvEpImages.asStateFlow()

    fun requestTVSeries(id: String) {
        viewModelScope.launch {
            val result = movieRepository.requestTvSeriesDetail(id)
            delay(5000)
            _tvSeriesDetail.update {
                result
            }
        }
    }

    fun requestTvEpActData(tvId: String, seNumber: Int) {
        viewModelScope.launch {

            val epData = async {
                movieRepository.requestTvEpDetail(tvId, seNumber)
            }.await()

            val epRecommend = async {
                movieRepository.requestTvRecommend(tvId)
            }.await()

            val epImages = async {
                movieRepository.requestTvImages(tvId, seNumber)
            }.await()

            _tvEpDetail.update {
                epData
            }
            if (epData.isSuccess) {
                epData.getOrNull()?.let {
                    updateCast(it.credits.cast)
                }
            }
            _tvEpRecommend.update {
                if (it.isSuccess) {
                    val list = epRecommend.getOrNull()?.results ?: mutableListOf()
                    Result.success(list)
                } else {
                    Result.failure(it.exceptionOrNull() ?: RuntimeException("Request fail"))
                }
            }

            _tvEpImages.update {
                if (it.isSuccess) {
                    val list = epImages.getOrNull()?.posters ?: mutableListOf()
                    val images = if (list.size > 20) {
                        list.subList(0, 20)
                    } else {
                        list
                    }
                    Result.success(images)
                } else {
                    Result.failure(it.exceptionOrNull() ?: RuntimeException("Request fail"))
                }
            }
        }
    }
}