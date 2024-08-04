package com.flight.movie.infra.master.ui.state

import com.flight.movie.infra.master.data.FilmItem
import com.google.android.gms.ads.nativead.NativeAd

/**
 * create by colin
 * 2024/7/2
 */
data class HomeCategoryUiState(
    val type: String,
    val title: String,
    val list: List<FilmItem>,
    val categoryString: String,
    val nativeAd: NativeAd? = null
)
