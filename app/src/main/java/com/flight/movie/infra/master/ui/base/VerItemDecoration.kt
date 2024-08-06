package com.flight.movie.infra.master.ui.base

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class VerItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    private val half = space / 2

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        (view.layoutParams as RecyclerView.LayoutParams).run {
            when (bindingAdapterPosition) {
                0 -> {
                    outRect.top = space
                    outRect.bottom = half
                }
                state.itemCount - 1 -> {
                    outRect.top = space
                    outRect.bottom = half
                }
                else -> {
                    outRect.top = half
                    outRect.bottom = half
                }
            }

        }
    }
}