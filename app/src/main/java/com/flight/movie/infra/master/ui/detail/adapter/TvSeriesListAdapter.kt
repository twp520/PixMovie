package com.flight.movie.infra.master.ui.detail.adapter

import android.content.Context
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.FilmItem
import com.flight.movie.infra.master.data.TvSeries
import com.flight.movie.infra.master.ui.formatVote

/**
 * create by colin
 * 2024/7/9
 */
class TvSeriesListAdapter : BaseQuickAdapter<TvSeries, QuickViewHolder>() {

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: TvSeries?) {
        item ?: return
        Glide.with(holder.itemView)
            .load(DataClient.getImageUrl(item.poster))
            .placeholder(R.drawable.shape_placeholder)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.getView(R.id.item_tv_series_poster))
        holder.setText(R.id.item_tv_series_name, item.name)
        holder.setText(R.id.item_tv_series_time, item.time)
        holder.setText(R.id.item_tv_series_vote, formatVote(item.vote))
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_tv_series, parent)
    }
}