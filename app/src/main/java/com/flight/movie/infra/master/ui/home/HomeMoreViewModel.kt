package com.flight.movie.infra.master.ui.home

import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.ui.base.BaseAdListViewModel
import com.flight.movie.infra.master.ui.state.MultipleItemState

/**
 * create by colin
 * 2024/7/7
 */
class HomeMoreViewModel(
    private val type: String,
    private val category: String
) : BaseAdListViewModel() {

    override suspend fun requestNetData(page: Int): List<MultipleItemState> {
        val filmResult = if (category == DataClient.CATEGORY_TRENDING) {
            DataClient.service.getTrendingPage(type, page)
        } else {
            DataClient.service.getListByCategoryPage(type, category, page)
        }
        return filmResult.results
    }

    override fun getFrom(): String {
        return AnalysisUtils.FROM_HOME_MORE_NATIVE
    }
}