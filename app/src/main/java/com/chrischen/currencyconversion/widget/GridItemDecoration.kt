package com.chrischen.currencyconversion.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class GridItemDecoration(
    private val spanCount: Int,
    private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        outRect.bottom = spacing

        //Header span
        if (position == 0) {
            outRect.top = spacing
            outRect.left = spacing
            outRect.right = spacing
            return
        }

        // no need consider header
        val column = (position - 1) % spanCount
        outRect.left =
            spacing - column * spacing / spanCount
        outRect.right =
            (column + 1) * spacing / spanCount
    }
}
