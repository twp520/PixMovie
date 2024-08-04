package com.flight.movie.infra.master.ui.home.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flight.movie.infra.master.data.FilmItem
import com.flight.movie.infra.master.ui.base.BaseAdListAdapter
import com.flight.movie.infra.master.ui.state.MultipleItemState
import kotlinx.coroutines.CoroutineScope

/**
 * create by colin
 * 2024/7/7
 */
class HomeMoreListAdapter(
    private val type: String,
    scope: CoroutineScope, from: String
) : BaseAdListAdapter(scope, from) {

    override fun createDataViewHolder(
        context: Context,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {
        return FilmItemViewHolder(parent.context, type, parent)
    }

    override fun bindDataItem(
        holder: RecyclerView.ViewHolder,
        position: Int,
        item: MultipleItemState
    ) {
        val film = item as FilmItem
        val filmItemViewHolder = holder as FilmItemViewHolder
        filmItemViewHolder.bindItem(film)
    }

}