package com.flight.movie.infra.master.ui.personal

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.ui.detail.params.TvEpDetailParams
import com.flight.movie.infra.master.ui.formatVote
import com.flight.movie.infra.master.ui.home.adapter.FilmItemViewHolder

/**
 * create by colin
 * 2024/7/6
 */
class WatchListViewHolder(context: Context, parent: ViewGroup?) :
    FilmItemViewHolder(context, "", parent) {

    val deleteMask: View? = itemView.findViewById(R.id.delete_mask)

    @SuppressLint("SetTextI18n")
    fun bindTv(tvEpDetailParams: TvEpDetailParams) {
        vote.text = formatVote(tvEpDetailParams.tvRate)
        type.text = DataClient.TYPE_TV
        name.text = tvEpDetailParams.name + "S${tvEpDetailParams.seNumber}"
        Glide.with(itemView.context)
            .load(DataClient.getImageUrl(tvEpDetailParams.tvPoster))
            .placeholder(R.drawable.shape_placeholder)
            .error(R.drawable.baseline_broken_image_24)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(poster)
    }
}