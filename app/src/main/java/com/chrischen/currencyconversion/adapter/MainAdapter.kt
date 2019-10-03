package com.chrischen.currencyconversion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.chrischen.currencyconversion.R
import com.chrischen.currencyconversion.databinding.ViewHolderCurrencyRateBinding
import com.chrischen.currencyconversion.viewholder.CurrencyRateHolder

/**
 * Created by chris chen on 2019-10-03.
 */

class MainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = arrayListOf<CurrencyRateItem>()

    fun setItems(items: List<CurrencyRateItem>) {
        this.items.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getCurrencyRateItem(position) ?: return

        (holder as CurrencyRateHolder).bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val userBinding =
            DataBindingUtil.inflate<ViewHolderCurrencyRateBinding>(
                layoutInflater,
                R.layout.view_holder_currency_rate,
                parent,
                false
            )
        return CurrencyRateHolder(userBinding)
    }

    private fun getCurrencyRateItem(position: Int): CurrencyRateItem? {
        return if (position in 0 until itemCount) {
            items[position]
        } else null
    }

    data class CurrencyRateItem(val currencyName: String, val currencyRate: Double)
}