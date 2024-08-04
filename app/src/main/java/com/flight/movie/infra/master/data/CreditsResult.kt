package com.flight.movie.infra.master.data

/**
 * create by colin
 * 2024/7/8
 */
data class CreditsResult(
    val cast: List<CastItem>,
    val crew: List<CastItem>
) {

    val getDirector: String
        get() = crew.firstOrNull()?.name ?: ""
}
