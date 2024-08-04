package com.flight.movie.infra.master.data

import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2024/7/10
 */
data class TvEpisodes(
    @SerializedName("air_date")
    val airDate: String,
    @SerializedName("episode_number")
    val epNumber: Int,
    @SerializedName("episode_type")
    val epType: String,
    val overview: String,
    val runtime: String,
    @SerializedName("still_path")
    val poster: String,
    @SerializedName("vote_average")
    val vote: Float,
    val crew: List<CastItem>
) {

    fun getDirector(): String {
        return crew.firstOrNull { it.job?.equals("Director", true) ?: false }?.name ?: "UnKnow"
    }
}
