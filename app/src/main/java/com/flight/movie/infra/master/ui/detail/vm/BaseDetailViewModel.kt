package com.flight.movie.infra.master.ui.detail.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flight.movie.infra.master.data.CastItem
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.MovieRepository
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.money.NativeLoader
import com.flight.movie.infra.master.ui.state.ListAdItem
import com.flight.movie.infra.master.ui.state.MultipleItemState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.math.min

/**
 * create by colin
 * 2024/7/23
 */
open class BaseDetailViewModel : ViewModel() {

    protected val movieRepository = MovieRepository(DataClient.service)

    val castData = MutableStateFlow(emptyList<MultipleItemState>())


    protected fun updateCast(cast: List<CastItem>) {
        val castList = mutableListOf<MultipleItemState>()
        val size = min(6, cast.size)
        castList.addAll(cast.subList(0, size))
        castList.add(ListAdItem())
        if (cast.size > size) {
            castList.addAll(cast.subList(size, cast.size))
        }
        castData.update {
            castList
        }
    }
}