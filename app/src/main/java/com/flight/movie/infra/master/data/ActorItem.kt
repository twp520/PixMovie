package com.flight.movie.infra.master.data

import com.flight.movie.infra.master.ui.MULTI_TYPE_DATA
import com.flight.movie.infra.master.ui.state.MultipleItemState
import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2024/7/3
 */
data class ActorItem(
    val id: String,
    val gender: Int,
    val name: String,
    @SerializedName("profile_path")
    val profile: String,
    @SerializedName("known_for")
    val knowForFilm: List<FilmItem>
) : MultipleItemState {

    override val type: Int
        get() = MULTI_TYPE_DATA

    fun getDisplayFilm(): String {
        if (knowForFilm.isEmpty())
            return ""
        return knowForFilm.joinToString {
            it.displayName
        }
    }
}