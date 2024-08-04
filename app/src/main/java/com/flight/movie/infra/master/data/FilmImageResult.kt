package com.flight.movie.infra.master.data

import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2024/7/8
 */
data class FilmImageResult(
    val posters: List<FilmImageItem>,
    val backdrops: List<FilmImageItem>
)

data class FilmImageItem(
    @SerializedName("aspect_ratio")
    val ratio: Float,
    val height: Int,
    val width: Int,
    @SerializedName("file_path")
    val filePath: String
)