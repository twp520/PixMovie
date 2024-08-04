package com.flight.movie.infra.master.data

import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2024/7/10
 */
data class TvEpDetail(
    val episodes: List<TvEpisodes>,
    val name: String,
    @SerializedName("season_number")
    val seNumber: Int,
    val credits: CreditsResult,
)