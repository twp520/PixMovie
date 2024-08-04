package com.flight.movie.infra.master.ui.state

import com.flight.movie.infra.master.data.Countries
import com.flight.movie.infra.master.data.Genres

/**
 * create by colin
 * 2024/7/6
 */
data class DiscoverUiState(
    val genresData: List<Genres> = emptyList(),
    val countriesData: List<Countries> = emptyList(),
    val yearData: List<String> = emptyList(),
    val showFilterType: Int = FILTER_TYPE_NONE
) {
    companion object {
        const val FILTER_TYPE_NONE = -1
        const val FILTER_TYPE_GENRES = 0
        const val FILTER_TYPE_COUNTRY = 1
        const val FILTER_TYPE_YEAR = 2
    }
}