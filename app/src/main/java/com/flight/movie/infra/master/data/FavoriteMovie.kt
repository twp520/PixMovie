package com.flight.movie.infra.master.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * create by colin
 * 2024/7/12
 */
@Entity
data class FavoriteMovie(
    @PrimaryKey
    val id: String,
    val name: String,
    val poster: String,
    val mediaType: String,
    val vote: Float,
)
