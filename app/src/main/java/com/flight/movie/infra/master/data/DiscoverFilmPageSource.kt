package com.flight.movie.infra.master.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * create by colin
 * 2024/7/5
 */
class DiscoverFilmPageSource(
    private val mediaType: String,
    private val checkedGenres: List<Genres>,
    private val checkedCountries: List<Countries>,
    private val checkedYears: List<String>
) : PagingSource<Int, FilmItem>() {

    override fun getRefreshKey(state: PagingState<Int, FilmItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmItem> {
        return try {
            val page = params.key ?: 1
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
            LoadResult.Page(
                peopleList.results,
                null,
                if (peopleList.page >= peopleList.totalPages) null else peopleList.page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}