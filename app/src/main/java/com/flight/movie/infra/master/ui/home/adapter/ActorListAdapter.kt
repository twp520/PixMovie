package com.flight.movie.infra.master.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.ActorItem
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.databinding.ItemPeopleBinding
import com.flight.movie.infra.master.ui.base.BaseAdListAdapter
import com.flight.movie.infra.master.ui.state.MultipleItemState
import kotlinx.coroutines.CoroutineScope

/**
 * create by colin
 * 2024/7/3
 */
class ActorListAdapter(scope: CoroutineScope, from: String) : BaseAdListAdapter(scope, from) {

    inner class ActorViewHolder(private val binding: ItemPeopleBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(actorItem: ActorItem) {

            binding.itemActorName.text = actorItem.name
            binding.itemActorGallery.text = actorItem.getDisplayFilm()
            Glide.with(itemView)
                .load(DataClient.getImageUrl(actorItem.profile))
                .placeholder(R.drawable.shape_placeholder)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.itemActorProfile)
        }
    }

    override fun createDataViewHolder(
        context: Context,
        parent: ViewGroup
    ): RecyclerView.ViewHolder {
        return ActorViewHolder(ItemPeopleBinding.inflate(LayoutInflater.from(context)))
    }

    override fun bindDataItem(
        holder: RecyclerView.ViewHolder,
        position: Int,
        item: MultipleItemState
    ) {
        val peopleItem = item as ActorItem
        (holder as ActorViewHolder).bind(peopleItem)
    }

}