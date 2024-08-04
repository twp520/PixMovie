package com.flight.movie.infra.master.ui.home.adapter

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.flight.movie.infra.master.data.FilmItem

/**
 * create by colin
 * 2024/7/2
 */
class FilmAdapter(data: List<FilmItem>, private val mediaType: String) :
    BaseQuickAdapter<FilmItem, FilmItemViewHolder>(data) {

    override fun onBindViewHolder(holder: FilmItemViewHolder, position: Int, item: FilmItem?) {
        item ?: return
        holder.bindItem(item)
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): FilmItemViewHolder {

        return FilmItemViewHolder(context, mediaType, parent)
    }
}