package com.flight.movie.infra.master.data

import androidx.recyclerview.widget.DiffUtil

/**
 * create by colin
 * 2024/7/3
 */
class ActorDiffCallback : DiffUtil.ItemCallback<ActorItem>() {

    override fun areItemsTheSame(oldItem: ActorItem, newItem: ActorItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ActorItem, newItem: ActorItem): Boolean {
        return oldItem == newItem
    }
}