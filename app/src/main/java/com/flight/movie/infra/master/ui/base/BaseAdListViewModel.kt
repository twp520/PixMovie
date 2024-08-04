package com.flight.movie.infra.master.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flight.movie.infra.master.money.InstallManager
import com.flight.movie.infra.master.ui.COL_COUNT
import com.flight.movie.infra.master.ui.state.ListAdItem
import com.flight.movie.infra.master.ui.state.MultipleItemState
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * create by colin
 * 2024/7/23
 */
abstract class BaseAdListViewModel : ViewModel() {

    private val _uiStateItem =
        MutableStateFlow<Result<List<MultipleItemState>>?>(
            null
        )
    val uiStateItem = _uiStateItem.asStateFlow()

    private val cachedDataItem = mutableListOf<MultipleItemState>()
    private var page: Int = 1

    val isRefresh get() = page == 1

    abstract suspend fun requestNetData(page: Int): List<MultipleItemState>

    abstract fun getFrom(): String

    fun loadUiData() {
        viewModelScope.launch {
            try {
                val rows = Firebase.remoteConfig.getLong("visibleRows").toInt()
                val size = rows * COL_COUNT

                val result = async { requestNetData(page) }.await()
                if (result.isEmpty()) {
                    _uiStateItem.update {
                        Result.success(result)
                    }
                    return@launch
                }
                page++
                val update = if (InstallManager.getRunB()) {
                    withContext(Dispatchers.Default) {
                        val itemList = mutableListOf<MultipleItemState>()
                        val actorList = result.toMutableList()
                        actorList.addAll(0, cachedDataItem)
                        cachedDataItem.clear()
                        actorList.addAll(result)
                        val mod = actorList.size % size
                        for (i in 0 until mod) {
                            cachedDataItem.add(actorList.removeLast())
                        }
                        actorList.forEachIndexed { index, actorItem ->
                            if (index > 0 && index % size == 0) {
                                itemList.add(ListAdItem())
                            }
                            itemList.add(actorItem)
                        }
                        itemList.add(ListAdItem())
                        return@withContext itemList
                    }
                } else {
                    result
                }
                _uiStateItem.update {
                    Result.success(update)
                }
            } catch (e: Exception) {
                _uiStateItem.update {
                    Result.failure(e)
                }
            }
        }
    }


    fun refresh() {
        _uiStateItem.update {
            null
        }
        page = 1
        loadUiData()
    }

}