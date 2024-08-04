package com.flight.movie.infra.master.ui.state

/**
 * create by colin
 * 2024/7/11
 */
interface FavoriteItemUiState {

    fun getFilmType(): String
    fun getTitle(): String
    fun getCover(): String?
    fun getRate(): String
}