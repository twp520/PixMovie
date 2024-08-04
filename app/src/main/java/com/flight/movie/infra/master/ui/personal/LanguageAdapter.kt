package com.flight.movie.infra.master.ui.personal

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.Countries

/**
 * create by colin
 * 2024/6/21
 */
class LanguageAdapter :
    BaseQuickAdapter<Countries, QuickViewHolder>() {

    var checkedLanguage: Int = -1

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Countries?) {
        Log.d("LanguageAdapter", "onBindViewHolder: $position , $item")
        item ?: return
        holder.setText(R.id.item_language_title, item.name)
        holder.itemView.isSelected = checkedLanguage == position
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_language, parent)
    }

    fun checkedCountry() = getItem(checkedLanguage)

}