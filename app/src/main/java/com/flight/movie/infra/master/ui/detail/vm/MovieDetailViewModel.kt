package com.flight.movie.infra.master.ui.detail.vm

import androidx.lifecycle.viewModelScope
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.FilmImageItem
import com.flight.movie.infra.master.data.MovieDetail
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * create by colin
 * 2024/7/8
 */
class MovieDetailViewModel : BaseDetailViewModel() {

    private val _movieDetail = MutableStateFlow<Result<MovieDetail>?>(null)
    val movieDetail = _movieDetail.asStateFlow()

    private val _movieImages = MutableStateFlow<Result<List<FilmImageItem>>>(
        Result.success(
            emptyList()
        )
    )
    val movieImages = _movieImages.asStateFlow()


    fun requestMovieDetail(id: String) {
        viewModelScope.launch {
            val result = async { movieRepository.requestMovieDetail(id) }.await()
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    updateCast(it.credits.cast)
                }
            }
            _movieDetail.update {
                result
            }
        }
    }

    fun requestMovieImages(id: String) {
        viewModelScope.launch {
            val filmImages = movieRepository.requestFilmImages(DataClient.TYPE_MOVIE, id)
            if (filmImages.isSuccess) {
                val list = filmImages.getOrNull()?.posters ?: mutableListOf()
                val images = if (list.size > 20) {
                    list.subList(0, 20)
                } else {
                    list
                }
                _movieImages.update {
                    Result.success(images)
                }
            } else {
                _movieImages.update {
                    Result.failure(filmImages.exceptionOrNull() ?: RuntimeException("Request fail"))
                }
            }
        }
    }


}