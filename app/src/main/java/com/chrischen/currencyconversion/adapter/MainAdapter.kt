package com.chrischen.currencyconversion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.chrischen.currencyconversion.R
import com.chrischen.currencyconversion.databinding.ViewHolderCurrencyRateBinding
import com.chrischen.currencyconversion.databinding.ViewHolderTopBinding
import com.chrischen.currencyconversion.viewholder.CurrencyRateHolder
import com.chrischen.currencyconversion.viewholder.TopHolder

/**
 * Created by chris chen on 2019-10-03.
 */

class MainAdapter(private val listener: TopHolder.Listener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = arrayListOf<Item>()

    fun setItems(items: List<Item>) {
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
        val item = getItem(position) ?: return

        when (item.type) {
            Item.TYPE_TOP -> {
                (holder as TopHolder).bind(item as Item.TopItem)
            }

            Item.TYPE_CURRENCY_RATE -> {
                (holder as CurrencyRateHolder).bind(item as Item.CurrencyRateItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            Item.TYPE_TOP -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val topBinding =
                    DataBindingUtil.inflate<ViewHolderTopBinding>(
                        layoutInflater,
                        R.layout.view_holder_top,
                        parent,
                        false
                    )
                TopHolder(topBinding, listener)
            }

            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val currencyRateBinding =
                    DataBindingUtil.inflate<ViewHolderCurrencyRateBinding>(
                        layoutInflater,
                        R.layout.view_holder_currency_rate,
                        parent,
                        false
                    )
                CurrencyRateHolder(currencyRateBinding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.type ?: Item.TYPE_CURRENCY_RATE
    }

    private fun getItem(position: Int): Item? {
        return if (position in 0 until itemCount) {
            items[position]
        } else null
    }

    sealed class Item(@ItemType val type: Int) {

        companion object {
            const val TYPE_TOP = 0
            const val TYPE_CURRENCY_RATE = 1

            @IntDef(TYPE_TOP, TYPE_CURRENCY_RATE)
            @Retention(AnnotationRetention.SOURCE)
            annotation class ItemType
        }

        data class TopItem(
            val currencyList: List<String>,
            val selectCurrency: String?,
            val amount: Double
        ) :
            Item(TYPE_TOP)

        data class CurrencyRateItem(val currencyName: String, val currencyRate: Double) :
            Item(TYPE_CURRENCY_RATE)
    }
}