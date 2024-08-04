package com.flight.movie.infra.master.ui.state

import com.flight.movie.infra.master.ui.MULTI_TYPE_AD

/**
 * create by colin
 * 2024/7/22
 */
data class ListAdItem(private val adType: Int = MULTI_TYPE_AD) : MultipleItemState {
    override val type: Int
        get() = MULTI_TYPE_AD
}
