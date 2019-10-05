package com.chrischen.currencyconversion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chrischen.currencyconversion.R
import com.chrischen.currencyconversion.viewholder.CurrencySelectionViewHolder

class CurrencySelectAdapter(
    private val listener: CurrencySelectionViewHolder.Listener,
    private val data: List<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CurrencySelectionViewHolder).bind((getItem(position)))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.view_holder_text, parent, false)
        return CurrencySelectionViewHolder(itemView, listener)
    }

    private fun getItem(position: Int): String? {
        return if (position in 0 until itemCount) {
            data[position]
        } else null
    }
}