package com.flight.movie.infra.master.data

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * create by colin
 * 2024/7/7
 */
class HomeMorePageSource(
    private val type: String,
    private val category: String
) : PagingSource<Int, FilmItem>() {

    override fun getRefreshKey(state: PagingState<Int, FilmItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmItem> {
        val page = params.key ?: 1
        return try {
            val filmResult = if (category == DataClient.CATEGORY_TRENDING) {
                DataClient.service.getTrendingPage(type, page)
            } else {
                DataClient.service.getListByCategoryPage(type, category, page)
            }
            LoadResult.Page(
                filmResult.results,
                null,
                if (filmResult.page >= filmResult.totalPages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}