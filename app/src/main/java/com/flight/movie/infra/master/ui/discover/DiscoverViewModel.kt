package com.flight.movie.infra.master.ui.discover

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.flight.movie.infra.master.data.Countries
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.Genres
import com.flight.movie.infra.master.data.MovieRepository
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.ui.base.BaseAdListViewModel
import com.flight.movie.infra.master.ui.state.DiscoverUiState
import com.flight.movie.infra.master.ui.state.MultipleItemState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Calendar

class DiscoverViewModel(private val mediaType: String) : BaseAdListViewModel() {

    val checkedGenres = mutableListOf<Genres>()
    val checkedCountries = mutableListOf<Countries>()
    val checkedYears = mutableListOf<String>()

    private val _discoverData = MutableStateFlow(DiscoverUiState())
    val discoverUiState = _discoverData.asStateFlow()

    private val movieRepository = MovieRepository(DataClient.service)


    fun initFilter(context: Context, type: String) {
        viewModelScope.launch {
            val countriesJob = withContext(Dispatchers.IO) {
                async {
                    val countries = mutableListOf<Countries>()
                    context.assets.open("countries.json").use {
                        val json = it.reader().readText()
                        val obj = JSONObject(json)
                        val array = obj.getJSONArray("countries")
                        for (i in 0 until array.length()) {
                            val item = array.getJSONObject(i)
                            countries.add(
                                Countries(
                                    item.getString("name"),
                                    item.getString("alpha-2"),
                                )
                            )
                        }
                    }
                    return@async countries
                }
            }
            val genresJob = async {
                val genresResult = movieRepository.requestGenresList(type)
                if (genresResult.isSuccess) {
                    genresResult.getOrNull() ?: mutableListOf()
                } else {
                    return@async withContext(Dispatchers.IO) {
                        val genres = mutableListOf<Genres>()
                        context.assets.open("genres.json").use {
                            val json = it.reader().readText()
                            val obj = JSONObject(json)
                            val array = if (type == DataClient.TYPE_MOVIE) {
                                obj.getJSONArray("movieGenres")
                            } else {
                                obj.getJSONArray("tvGenres")
                            }
                            for (i in 0 until array.length()) {
                                val item = array.getJSONObject(i)
                                val id = item.getInt("id")
                                val name = item.getString("name")
                                genres.add(Genres().apply {
                                    this.id = id
                                    this.name = name
                                })
                            }
                            genres
                        }
                    }
                }
            }
            val yearsJob = async {
                val years = mutableListOf<String>()
                var yearNow = Calendar.getInstance().get(Calendar.YEAR)
                for (i in 0 until 20) {
                    years.add(yearNow.toString())
                    yearNow--
                }
                years
            }
            _discoverData.update {
                DiscoverUiState(
                    genresJob.await(),
                    countriesJob.await(), yearsJob.await()
                )
            }
        }
    }

    fun filterButtonClicked(filterType: Int) {
        if (filterType == _discoverData.value.showFilterType) {
            _discoverData.update {
                it.copy(showFilterType = DiscoverUiState.FILTER_TYPE_NONE)
            }
            return
        }
        _discoverData.update {
            it.copy(showFilterType = filterType)
        }
    }

    fun filterOK() {
        _discoverData.update {
            it.copy(showFilterType = DiscoverUiState.FILTER_TYPE_NONE)
        }
    }

    override suspend fun requestNetData(page: Int): List<MultipleItemState> {
        val queryMap = HashMap<String, String>()
        queryMap["page"] = page.toString()
        if (checkedGenres.isNotEmpty()) {
            queryMap["with_genres"] = checkedGenres.joinToString(separator = "|") {
                it.name
            }
        }
        if (checkedCountries.isNotEmpty()) {
            queryMap["region"] = checkedCountries.joinToString(separator = "|") { it.code }
        }
        if (checkedYears.isNotEmpty()) {
            queryMap["year"] = checkedYears.first()
        }
        Log.d("DiscoverFilmPageSource", "load: page = $page , queryMap=$queryMap")

        val peopleList = DataClient.service.getDiscoverList(mediaType, queryMap)
        return peopleList.results
    }

    override fun getFrom(): String {
        return if (mediaType == DataClient.TYPE_MOVIE)
            AnalysisUtils.FROM_DISCOVER_MOVIE_NATIVE else AnalysisUtils.FROM_DISCOVER_TV_NATIVE
    }

}