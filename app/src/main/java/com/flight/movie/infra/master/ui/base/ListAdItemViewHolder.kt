package com.flight.movie.infra.master.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.money.NativeLoader
import com.flight.movie.infra.master.money.TemplateView
import kotlinx.coroutines.CoroutineScope

/**
 * create by colin
 * 2024/7/22
 */
@SuppressLint("InflateParams")
class ListAdItemViewHolder(private val context: Context, scope: CoroutineScope, from: String) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.item_list_ad, null, false)
    ) {

    private val nativeLoader = NativeLoader(
        context.getString(R.string.native_test),
        scope, from, true
    ).apply {
        refreshAd(context, null)
    }

    fun bindAd() {
        nativeLoader.refreshAd(context) {
            itemView.findViewById<TemplateView>(R.id.item_list_ad_view).apply {
                setNativeAd(it)
            }
        }
    }

    fun destroy() {
        nativeLoader.destroy()
    }

}