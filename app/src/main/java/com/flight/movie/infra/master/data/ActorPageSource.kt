package com.flight.movie.infra.master.data

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * create by colin
 * 2024/7/3
 */
class ActorPageSource : PagingSource<Int, ActorItem>() {

    override fun getRefreshKey(state: PagingState<Int, ActorItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ActorItem> {
        val page = params.key ?: 1
        return try {
            val peopleList = DataClient.service.getPeopleList(page)
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