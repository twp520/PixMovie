package com.flight.movie.infra.master.ui.personal

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.flight.movie.infra.master.data.FavoriteMovie
import com.flight.movie.infra.master.ui.detail.params.TvEpDetailParams
import com.flight.movie.infra.master.ui.toFilm

/**
 * create by colin
 * 2024/7/6
 */
@SuppressLint("NotifyDataSetChanged")
class WatchListAdapter : BaseQuickAdapter<Any, WatchListViewHolder>() {

    var isEditModel: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: WatchListViewHolder, position: Int, item: Any?) {
        item ?: return
        if (item is FavoriteMovie) {
            holder.bindItem(toFilm(item))
        } else if (item is TvEpDetailParams) {
            holder.bindTv(item)
        }
        holder.deleteMask?.isVisible = isEditModel
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): WatchListViewHolder {
        return WatchListViewHolder(context, parent)
    }
}