package com.flight.movie.infra.master.ui.detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.CastItem
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.databinding.ItemPeopleBinding
import com.flight.movie.infra.master.ui.base.BaseAdListAdapter
import com.flight.movie.infra.master.ui.startPeopleDetail
import com.flight.movie.infra.master.ui.state.MultipleItemState
import kotlinx.coroutines.CoroutineScope

/**
 * create by colin
 * 2024/7/8
 */
class CastListAdapter(scope: CoroutineScope, from: String) : BaseAdListAdapter(scope, from) {

    override fun createDataViewHolder(
        context: Context,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {
        return CastViewHolder(
            ItemPeopleBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun bindDataItem(
        holder: RecyclerView.ViewHolder,
        position: Int,
        item: MultipleItemState
    ) {
        if (holder is CastViewHolder && item is CastItem) {
            holder.bind(item)
        }
    }

    inner class CastViewHolder(private val binding: ItemPeopleBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(actorItem: CastItem) {

            binding.itemActorName.text = actorItem.name
            binding.itemActorGallery.text = actorItem.character
            Glide.with(itemView)
                .load(DataClient.getImageUrl(actorItem.profile))
                .placeholder(R.drawable.shape_placeholder)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.itemActorProfile)
            itemView.setOnClickListener {
                startPeopleDetail(it.context, actorItem.id)
            }
        }
    }
}