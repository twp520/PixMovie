package com.flight.movie.infra.master.data

import com.flight.movie.infra.master.ui.MULTI_TYPE_DATA
import com.flight.movie.infra.master.ui.state.MultipleItemState
import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2024/7/9
 */
data class TvSeries(
    val id: String,
    val name: String,
    @SerializedName("poster_path")
    val poster: String,
    @SerializedName("vote_average")
    val vote: Float,
    @SerializedName("air_date")
    val time: String,
    @SerializedName("season_number")
    val seasonNumber: Int
) : MultipleItemState {
    override val type: Int
        get() = MULTI_TYPE_DATA
}
