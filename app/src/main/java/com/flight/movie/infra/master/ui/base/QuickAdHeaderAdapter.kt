package com.flight.movie.infra.master.ui.base

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.flight.movie.infra.master.money.InstallManager
import com.flight.movie.infra.master.ui.state.ListAdItem
import kotlinx.coroutines.CoroutineScope

/**
 * create by colin
 * 2024/7/23
 */
class QuickAdHeaderAdapter(
    private val scope: CoroutineScope, private val from: String
) : BaseQuickAdapter<ListAdItem, ListAdItemViewHolder>(
    if (InstallManager.getRunB()) listOf(
        ListAdItem()
    ) else emptyList()
) {
    override fun onBindViewHolder(holder: ListAdItemViewHolder, position: Int, item: ListAdItem?) {
        item ?: return
        holder.bindAd()
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ListAdItemViewHolder {
        return ListAdItemViewHolder(context, scope, from)
    }

    override fun isFullSpanItem(itemType: Int): Boolean {
        return true
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is ListAdItemViewHolder) {
            holder.destroy()
        }
    }
}