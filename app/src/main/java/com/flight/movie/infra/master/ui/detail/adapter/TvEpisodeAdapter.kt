package com.flight.movie.infra.master.ui.detail.adapter

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.TvEpisodes

/**
 * create by colin
 * 2024/7/10
 */
class TvEpisodeAdapter(data: List<TvEpisodes>) :
    BaseQuickAdapter<TvEpisodes, QuickViewHolder>(data) {

    var checkedPosition = 0

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: TvEpisodes?) {
        item ?: return
        holder.setText(R.id.item_ep, item.epNumber.toString())
        holder.setSelected(R.id.item_ep, checkedPosition == position)
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_tv_episode, parent)
    }
}