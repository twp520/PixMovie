package com.flight.movie.infra.master.ui.detail.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.FilmImageItem

/**
 * create by colin
 * 2024/7/9
 */
class ImagePageAdapter(data: List<FilmImageItem>) :
    BaseQuickAdapter<FilmImageItem, QuickViewHolder>(data) {

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: FilmImageItem?) {
        item ?: return
        val imageView = holder.getView<ImageFilterView>(R.id.item_image_iv)
        Glide.with(context)
            .load(DataClient.getImageUrl(item.filePath))
            .placeholder(R.drawable.shape_placeholder)
            .error(R.drawable.baseline_broken_image_24)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_image_page, parent)
    }
}