package com.flight.movie.infra.master.data

import androidx.recyclerview.widget.DiffUtil

/**
 * create by colin
 * 2024/7/3
 */
class FilmDiffCallback : DiffUtil.ItemCallback<FilmItem>() {

    override fun areItemsTheSame(oldItem: FilmItem, newItem: FilmItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FilmItem, newItem: FilmItem): Boolean {
        return oldItem == newItem
    }
}