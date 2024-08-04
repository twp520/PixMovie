package com.flight.movie.infra.master.data

import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2024/7/8
 */
data class MovieDetail(
    val title: String,
    val poster: String,
    val overview: String,
    val id: String,
    @SerializedName("imdb_id")
    val imdbId: String,
    val budget: String,
    val genres: List<Genres>,
    @SerializedName("production_countries")
    val productionCountries: List<Countries>,
    @SerializedName("release_date")
    val releaseDate: String,
    val runtime: String,
    val status: String,
    val revenue: String,
    @SerializedName("vote_average")
    val vote: Float,
    val recommendations: ApiPage<FilmItem>,
    val credits: CreditsResult,
) {

    val displayCountry: String
        get() = productionCountries.firstOrNull()?.name ?: "UnKnow"

    val displayGenres: String
        get() = genres.joinToString(" / ") { it.name }
}
