package com.chrischen.currencyconversion.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chrischen.currencyconversion.R
import com.chrischen.currencyconversion.extension.bindView

class CurrencySelectionViewHolder(itemView: View, listener: Listener) :
    RecyclerView.ViewHolder(itemView) {

    private var currency: String? = null

    init {
        itemView.setOnClickListener {
            listener.onCurrencyClick(currency)
        }
    }

    private val optionTextView: TextView by bindView(R.id.currencyName)

    fun bind(currency: String?) {
        this.currency = currency
        optionTextView.text = currency
    }

    interface Listener {
        fun onCurrencyClick(currency: String?)
    }
}