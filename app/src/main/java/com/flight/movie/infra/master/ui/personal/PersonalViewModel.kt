package com.flight.movie.infra.master.ui.personal

import androidx.lifecycle.ViewModel
import com.flight.movie.infra.master.data.FilmItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PersonalViewModel : ViewModel() {

    private val _favListData = MutableStateFlow<List<FilmItem>>(emptyList())
    val favListData = _favListData.asStateFlow()

}