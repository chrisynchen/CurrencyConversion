package com.chrischen.currencyconversion.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.chrischen.currencyconversion.R

class HeightLimitRecyclerView : RecyclerView {
    private var maxHeight: Int = 0

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.HeightLimitRecyclerView)
        maxHeight = arr.getLayoutDimension(R.styleable.HeightLimitRecyclerView_maxHeight, maxHeight)
        arr.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var customHeightMeasureSpec = heightMeasureSpec
        if (maxHeight > 0) {
            customHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthMeasureSpec, customHeightMeasureSpec)
    }
}