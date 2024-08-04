package com.flight.movie.infra.master.data

import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2024/7/11
 */
data class PeopleDetail(
    val id: String,
    val name: String,
    @SerializedName("known_for_department")
    val department: String,
    val biography: String,
    val birthday: String,
    @SerializedName("profile_path")
    val profile: String,
    @SerializedName("movie_credits")
    val movies: PeopleCreditsResult,
    @SerializedName("tv_credits")
    val tvs: PeopleCreditsResult,
) {

    fun getFilmList(): List<FilmItem> {
        val films = mutableListOf<FilmItem>()
        movies.cast.forEach {
            films.add(it.copy(mediaType = DataClient.TYPE_MOVIE))
        }
        tvs.cast.forEach {
            films.add(it.copy(mediaType = DataClient.TYPE_TV))
        }
        return films
    }
}
