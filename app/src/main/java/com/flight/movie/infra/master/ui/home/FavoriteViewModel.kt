package com.flight.movie.infra.master.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.FavoriteMovie
import com.flight.movie.infra.master.ui.detail.params.TvEpDetailParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * create by colin
 * 2024/7/11
 */
class FavoriteViewModel : ViewModel() {

    private val filmDao = DataClient.appDB.favFilmDao()
    private val tvDao = DataClient.appDB.favTvDao()


    val favoriteData by lazy {
        filmDao.queryFavFilm().combine(tvDao.queryFavFilm()) { film, tv ->
            Log.d(
                "FavoriteViewModel",
                "query favorite :Thread->${Thread.currentThread().name}," +
                        " film size = ${film.size} , tv size = ${tv.size} "
            )
            val uiStateList = mutableListOf<Any>()
            uiStateList.addAll(film)
            uiStateList.addAll(tv)
            uiStateList
        }.flowOn(Dispatchers.IO)
    }

    fun favoriteFilm(filmItem: FavoriteMovie) {
        viewModelScope.launch(Dispatchers.IO) {
            filmDao.insertFilm(filmItem)
        }
    }

    fun unFavoriteFilm(filmItem: FavoriteMovie) {
        viewModelScope.launch(Dispatchers.IO) {
            filmDao.deleteFilm(filmItem)
        }
    }

    fun favoriteTv(tvEpDetailParams: TvEpDetailParams) {
        viewModelScope.launch(Dispatchers.IO) {
            tvDao.insertFilm(tvEpDetailParams)
        }
    }

    fun unFavoriteTv(tvEpDetailParams: TvEpDetailParams) {
        viewModelScope.launch(Dispatchers.IO) {
            tvDao.deleteFilm(tvEpDetailParams)
        }
    }

    fun isFavorite(filmItem: FavoriteMovie): Flow<Boolean> {
        return filmDao.isFavorite(filmItem.id).map {
            it != null
        }.flowOn(Dispatchers.IO)
    }

    fun isFavorite(tvEpDetailParams: TvEpDetailParams): Flow<Boolean> {
        return tvDao.isFavorite(tvEpDetailParams.tvId, tvEpDetailParams.seNumber).map {
            it != null
        }.flowOn(Dispatchers.IO)
    }
}