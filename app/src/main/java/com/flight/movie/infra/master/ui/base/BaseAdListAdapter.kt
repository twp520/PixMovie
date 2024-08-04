package com.flight.movie.infra.master.ui.base

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.flight.movie.infra.master.ui.MULTI_TYPE_AD
import com.flight.movie.infra.master.ui.state.MultipleItemState
import kotlinx.coroutines.CoroutineScope

/**
 * create by colin
 * 2024/7/23
 */
abstract class BaseAdListAdapter(
    private val scope: CoroutineScope, private val from: String
) : BaseQuickAdapter<MultipleItemState, RecyclerView.ViewHolder>() {

    abstract fun createDataViewHolder(
        context: Context,
        parent: ViewGroup,
    ): RecyclerView.ViewHolder

    abstract fun bindDataItem(
        holder: RecyclerView.ViewHolder,
        position: Int,
        item: MultipleItemState
    )

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        item: MultipleItemState?
    ) {
        item ?: return
        if (item.type == MULTI_TYPE_AD) {
            (holder as ListAdItemViewHolder).bindAd()
        } else {
            bindDataItem(holder, position, item)
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == MULTI_TYPE_AD) {
            ListAdItemViewHolder(context, scope, from)
        } else {
            createDataViewHolder(context, parent)
        }
    }

    override fun isFullSpanItem(itemType: Int): Boolean {
        return itemType == MULTI_TYPE_AD
    }

    override fun getItemViewType(position: Int, list: List<MultipleItemState>): Int {
        return list[position].type
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is ListAdItemViewHolder) {
            holder.destroy()
        }
    }
}