package com.flight.movie.infra.master.ui.home.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.FilmItem
import com.flight.movie.infra.master.money.TemplateView
import com.flight.movie.infra.master.ui.base.GridItemDecoration
import com.flight.movie.infra.master.ui.startDetail
import com.flight.movie.infra.master.ui.state.HomeCategoryUiState

/**
 * create by colin
 * 2024/7/2
 */
class HomeCategoryAdapter(private val mediaType: String) :
    BaseQuickAdapter<HomeCategoryUiState, QuickViewHolder>() {

    var onFilmItemClickListener: ((film: FilmItem) -> Unit)? = null

    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: HomeCategoryUiState?
    ) {
        item ?: return
        holder.setText(R.id.item_home_category_title, item.title)
        val filmAdapter = FilmAdapter(item.list, mediaType)
        val listView = holder.getView<RecyclerView>(R.id.item_home_category_list)
        if (listView.itemDecorationCount < 1) {
            listView.addItemDecoration(
                GridItemDecoration(
                    context.resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin),
                    3
                )
            )
        }

        listView.layoutManager = GridLayoutManager(context, 3)
        listView.adapter = filmAdapter
        filmAdapter.setOnItemClickListener { adapter, view, filmPosition ->
            filmAdapter.getItem(filmPosition)?.let {
                onFilmItemClickListener?.invoke(it)
            }
        }
        holder.setGone(R.id.adView, item.nativeAd == null)
        item.nativeAd?.let {
            holder.getView<TemplateView>(R.id.adView).setNativeAd(it)
        }
    }


    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_home_category, parent)
    }
}