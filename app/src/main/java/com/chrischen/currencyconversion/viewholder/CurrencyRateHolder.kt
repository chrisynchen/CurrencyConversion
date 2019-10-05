package com.chrischen.currencyconversion.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.chrischen.currencyconversion.adapter.MainAdapter
import com.chrischen.currencyconversion.databinding.ViewHolderCurrencyRateBinding

/**
 * Created by chris chen on 2019-10-03.
 */
class CurrencyRateHolder(private val viewDataBinding: ViewHolderCurrencyRateBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {

    fun bind(currencyItem: MainAdapter.Item.CurrencyRateItem) {
        viewDataBinding.apply {
            currencyRateItem = currencyItem
            executePendingBindings()
        }
    }
}