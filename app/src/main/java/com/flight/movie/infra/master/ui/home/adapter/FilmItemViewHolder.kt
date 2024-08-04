package com.flight.movie.infra.master.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.FilmItem
import com.flight.movie.infra.master.ui.formatVote

/**
 * create by colin
 * 2024/7/3
 */
open class FilmItemViewHolder(
    private val context: Context,
    private val mediaType: String,
    parent: ViewGroup?
) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.item_film_main, parent, false)
    ) {

    protected val vote: TextView = itemView.findViewById(R.id.item_film_vote)
    protected val type: TextView = itemView.findViewById(R.id.item_film_type)
    protected val name: TextView = itemView.findViewById(R.id.item_film_name)
    protected val poster: ImageView = itemView.findViewById(R.id.item_film_poster)

    fun bindItem(filmItem: FilmItem) {
        vote.text = formatVote(filmItem.vote)
        type.text = filmItem.mediaType ?: this.mediaType
        name.text = filmItem.displayName
        Glide.with(context)
            .load(DataClient.getImageUrl(filmItem.poster))
            .placeholder(R.drawable.shape_placeholder)
            .error(R.drawable.baseline_broken_image_24)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(poster)
    }
}