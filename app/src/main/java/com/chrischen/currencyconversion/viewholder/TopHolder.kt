package com.chrischen.currencyconversion.viewholder

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chrischen.currencyconversion.R
import com.chrischen.currencyconversion.adapter.MainAdapter
import com.chrischen.currencyconversion.databinding.ViewHolderTopBinding
import com.chrischen.currencyconversion.extension.bindView
import com.google.android.material.textfield.TextInputEditText

class TopHolder(private val viewDataBinding: ViewHolderTopBinding, listener: Listener) :
    RecyclerView.ViewHolder(viewDataBinding.root) {

    private val editCurrencyEditText: TextInputEditText by bindView(R.id.editCurrencyEditText)

    private val selectCurrencyTextView: TextView by bindView(R.id.selectCurrencyTextView)

    private val encryptionTextView: TextView by bindView(R.id.encryptionTextView)

    private val decryptionTextView: TextView by bindView(R.id.decryptionTextView)

    private var topItem: MainAdapter.Item.TopItem? = null

    init {
        selectCurrencyTextView.setOnClickListener {
            listener.showCurrencyListDialog(topItem?.currencyList)
        }

        editCurrencyEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(amount: CharSequence?, p1: Int, p2: Int, p3: Int) {
                listener.onAmountChanged(amount)
            }
        })

        encryptionTextView.setOnClickListener {
            listener.onEncryption()
        }

        decryptionTextView.setOnClickListener {
            listener.onDecryption()
        }
    }

    fun bind(topItem: MainAdapter.Item.TopItem) {
        this.topItem = topItem

        viewDataBinding.apply {
            this.topItem = topItem
        }
    }

    interface Listener {
        fun onAmountChanged(amountText: CharSequence?)
        fun showCurrencyListDialog(currencyList: List<String>?)
        fun onEncryption()
        fun onDecryption()
    }
}