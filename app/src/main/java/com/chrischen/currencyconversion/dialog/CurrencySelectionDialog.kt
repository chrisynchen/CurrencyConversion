package com.chrischen.currencyconversion.dialog

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.chrischen.currencyconversion.R
import com.chrischen.currencyconversion.adapter.CurrencySelectAdapter
import com.chrischen.currencyconversion.viewholder.CurrencySelectionViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class CurrencySelectionDialog(
    context: Context,
    listener: Listener,
    list: List<String>
) : BaseBottomSheetDialog(context) {

    val listener: CurrencySelectionViewHolder.Listener =
        object : CurrencySelectionViewHolder.Listener {
            override fun onCurrencyClick(currency: String?) {
                dismiss()
                listener.onCurrencyClick(currency)
            }
        }

    init {
        setContentView(R.layout.dialog_currency_selection)
        val adapter = CurrencySelectAdapter(this.listener, list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    fun setOnClickOutsideDismiss(dismiss: Boolean): CurrencySelectionDialog {
        clickOutsideDismissDialog = dismiss
        return this
    }

    interface Listener : CurrencySelectionViewHolder.Listener
}