package com.flight.movie.infra.master.data

import com.google.gson.annotations.SerializedName

/**
 * create by colin
 * 2024/7/2
 */
data class ApiPage<T>(
    val page: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int,
    val results: List<T>
)
