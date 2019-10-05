package com.chrischen.currencyconversion.extension

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by chris chen on 2019-10-03.
 */
fun <T : View> RecyclerView.ViewHolder.bindView(@IdRes resId: Int): Lazy<T> {
    return lazy {
        itemView.findViewById<T>(resId)
    }
}