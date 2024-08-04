package com.flight.movie.infra.master.ui.detail.vm

import androidx.lifecycle.viewModelScope
import com.flight.movie.infra.master.data.PeopleDetail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * create by colin
 * 2024/7/23
 */
class ActorDetailViewModel :BaseDetailViewModel(){

    private val _peopleDetail = MutableStateFlow<Result<PeopleDetail>?>(null)
    val peopleDetail = _peopleDetail.asStateFlow()


    fun requestPeopleDetail(id: String) {
        viewModelScope.launch {
            val result = movieRepository.requestPeopleDetail(id)
            delay(1500)
            _peopleDetail.update {
                result
            }
        }
    }
}