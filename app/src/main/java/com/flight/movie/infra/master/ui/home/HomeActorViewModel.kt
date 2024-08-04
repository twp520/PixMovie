package com.flight.movie.infra.master.ui.home

import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.ui.base.BaseAdListViewModel
import com.flight.movie.infra.master.ui.state.MultipleItemState

/**
 * create by colin
 * 2024/7/21
 */
class HomeActorViewModel : BaseAdListViewModel() {

    override suspend fun requestNetData(page: Int): List<MultipleItemState> {
        return DataClient.service.getPeopleList(page).results
    }

    override fun getFrom(): String {
        return AnalysisUtils.FROM_HOME_CATEGORY_PEOPLE_NATIVE
    }
}