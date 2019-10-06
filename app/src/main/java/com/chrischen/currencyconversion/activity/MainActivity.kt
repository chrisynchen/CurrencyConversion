package com.chrischen.currencyconversion.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.chrischen.currencyconversion.R
import com.chrischen.currencyconversion.adapter.MainAdapter
import com.chrischen.currencyconversion.dagger.DaggerMainComponent
import com.chrischen.currencyconversion.databinding.ActivityMainBinding
import com.chrischen.currencyconversion.dialog.CurrencySelectionDialog
import com.chrischen.currencyconversion.storage.CurrencyPreference
import com.chrischen.currencyconversion.viewholder.TopHolder
import com.chrischen.currencyconversion.viewmodel.MainViewModel
import com.chrischen.currencyconversion.widget.GridItemDecoration
import javax.inject.Inject

class MainActivity : AppCompatActivity(), TopHolder.Listener {

    companion object {
        private const val SPAN_TOP = 3
        private const val SPAN_ITEM = 1
        private const val SPAN_COUNT = SPAN_TOP
        private const val GRID_ITEM_MARGIN = 12
    }

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    private val adapter = MainAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component = DaggerMainComponent.builder()
            .currencyPreference(CurrencyPreference)
            .build()
        component.inject(this)
        initViews()
    }

    private fun initViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            lifecycleOwner = this@MainActivity
            vm = viewModel
            recyclerView.let {
                it.layoutManager =
                    GridLayoutManager(this@MainActivity, SPAN_TOP).also { layoutManager ->
                        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return when (adapter.getItemViewType(position)) {
                                    MainAdapter.Item.TYPE_TOP -> SPAN_TOP
                                    else -> SPAN_ITEM
                                }
                            }
                        }
                    }
                it.addItemDecoration(GridItemDecoration(SPAN_COUNT, GRID_ITEM_MARGIN))
                it.adapter = adapter
            }
        }

        viewModel.currencyItems.observe(this, Observer {
            adapter.setItems(it)
        })

        viewModel.logMessage.observe(this, Observer {
            Log.d(it.first, it.second)
        })
    }

    private val currencySelectionListener = object : CurrencySelectionDialog.Listener {
        override fun onCurrencyClick(currency: String?) {
            viewModel.changeCurrency(currency)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onRefresh()
    }

    override fun onAmountChanged(amountText: CharSequence?) {
        viewModel.changeAmount(amountText)
    }

    override fun showCurrencyListDialog(currencyList: List<String>?) {
        currencyList?.let {
            CurrencySelectionDialog(this, currencySelectionListener, it)
                .setOnClickOutsideDismiss(true)
                .show()
        }
    }
}
