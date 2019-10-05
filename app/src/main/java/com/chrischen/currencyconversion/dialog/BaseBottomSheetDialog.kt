package com.chrischen.currencyconversion.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.chrischen.currencyconversion.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

open class BaseBottomSheetDialog protected constructor(context: Context) :
    BottomSheetDialog(context, R.style.BottomPopupDialogTheme) {

    internal var clickOutsideDismissDialog = false
    private var disableDragging = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnShowListener {
            val bottomSheet =
                findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if (null != bottomSheet) {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isHideable = false
                this@BaseBottomSheetDialog.disableDragging(behavior)
            }
            if (!clickOutsideDismissDialog) {
                this@BaseBottomSheetDialog.disableClickOutsideToDismiss()
            }
        }
    }

    private fun disableDragging(behavior: BottomSheetBehavior<FrameLayout>) {
        behavior.skipCollapsed = disableDragging
        behavior.bottomSheetCallback = bottomSheetCallback
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                cancel()
            } else if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

    }

    private fun disableClickOutsideToDismiss() {
        val touchOutsideView = findViewById<View>(com.google.android.material.R.id.touch_outside)
        touchOutsideView?.setOnClickListener(null)
    }

    override fun onBackPressed() {
        dismiss()
    }
}