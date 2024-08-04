package com.flight.movie.infra.master.ui.detail.vm

import androidx.lifecycle.ViewModel
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.ui.detail.params.PlayerParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * create by colin
 * 2024/7/17
 */
class PlayerViewModel : ViewModel() {

    private val baseUrl = "https://multiembed.mov/"
    // private val baseUrl = "https://vidsrc.xyz/embed/movie"

    val linesData = MutableStateFlow(0)

    fun getPlayUrl(playerParams: PlayerParams): String {
        return if (playerParams.filmType == DataClient.TYPE_MOVIE) {
            baseUrl + "?video_id=${playerParams.filmId}"
            // baseUrl + "?imdb=${playerParams.filmId}"
        } else {
            baseUrl + "?video_id=${playerParams.filmId}&s=${playerParams.sNumber}&e=${playerParams.eNumber}&tmdb=1"
        }
    }

    fun chooseLines(position: Int) {
        linesData.update {
            position
        }
    }
}