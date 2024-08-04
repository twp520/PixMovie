package com.flight.movie.infra.master.ui.base

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class GridItemDecoration(
    private val edge: Int, private val colCount: Int,
    private val isEdgeZero: Boolean = false
) :
    RecyclerView.ItemDecoration() {

    private val half = edge / 2

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        (view.layoutParams as RecyclerView.LayoutParams).run {
            outRect.top = if (bindingAdapterPosition in 0 until colCount) {
                edge
            } else {
                half
            }
            outRect.bottom =
                if (bindingAdapterPosition in state.itemCount - colCount..state.itemCount) {
                    edge
                } else {
                    half
                }
            when {
                bindingAdapterPosition % colCount == 0 -> {
                    outRect.left = if (isEdgeZero) 0 else edge
                    outRect.right = half
                }

                bindingAdapterPosition % colCount == colCount - 1 -> {
                    outRect.right = if (isEdgeZero) 0 else edge
                    outRect.left = half
                }

                else -> {
                    outRect.left = half
                    outRect.right = half
                }
            }
        }
    }
}