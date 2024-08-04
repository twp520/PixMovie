package com.flight.movie.infra.master.data

import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2024/7/9
 */
data class TVSeriesDetail(
    val name: String,
    @SerializedName("poster_path")
    val poster: String,
    val overview: String,
    val id: String,
    val genres: List<Genres>,
    val seasons: List<TvSeries>,
    @SerializedName("production_countries")
    val productionCountries: List<Countries>,
) {
    val displayCountry: String
        get() = productionCountries.firstOrNull()?.name ?: "UnKnow"

    val displayGenres: String
        get() = genres.joinToString(" / ") { it.name }
}