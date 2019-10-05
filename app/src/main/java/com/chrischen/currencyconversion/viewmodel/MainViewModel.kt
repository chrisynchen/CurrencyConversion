package com.chrischen.currencyconversion.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chrischen.currencyconversion.adapter.MainAdapter
import com.chrischen.currencyconversion.network.response.CurrencyListDetail
import com.chrischen.currencyconversion.network.response.ExchangeRate
import com.chrischen.currencyconversion.repository.ICurrencyRepository
import com.chrischen.currencyconversion.utility.RxUtil
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import retrofit2.Response

/**
 * Created by chris chen on 2019-10-02.
 */

class MainViewModel(private val currencyRepository: ICurrencyRepository) : BaseViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _currencyItems = MutableLiveData<List<MainAdapter.Item>>()
    val currencyItems: LiveData<List<MainAdapter.Item>>
        get() = _currencyItems

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean>
        get() = _progressVisibility

    private var selectCurrency = ""

    private var inputAmount = 1.toDouble()

    fun onRefresh() {
        onCurrencyChanged()
    }

    fun onCurrencyChanged(
        currency: String = this.selectCurrency,
        inputAmount: Double = this.inputAmount
    ) {
        selectCurrency = currency
        this.inputAmount = inputAmount;

        val disposable = Single.zip(
            currencyRepository.fetchCurrencyList(),
            currencyRepository.fetchRecentExchangeRate(),
            BiFunction<Response<CurrencyListDetail?>, Response<ExchangeRate?>, List<MainAdapter.Item>> { currencyListDetailResponse, exchangeRateResponse ->

                val currencyList =
                    currencyListDetailResponse.body()?.currencies?.keys?.toList()
                if (selectCurrency.isEmpty()) {
                    selectCurrency = exchangeRateResponse.body()?.source ?: ""
                }
                val currencyItem = if (currencyList == null) {
                    null
                } else {
                    MainAdapter.Item.TopItem(currencyList, selectCurrency, inputAmount)
                }

                val source = exchangeRateResponse.body()?.source ?: ""
                val selectCurrencyRate =
                    exchangeRateResponse.body()?.quotes?.get(source + selectCurrency)
                        ?: 0.toDouble()

                val currencyRateItem = exchangeRateResponse.body()?.quotes?.map { entry ->
                    val desc = if (entry.key.startsWith(source)) {
                        "$selectCurrency to " + entry.key.substring(
                            source.length,
                            entry.key.length
                        )
                    } else {
                        selectCurrency + " to " + entry.key
                    }
                    MainAdapter.Item.CurrencyRateItem(
                        desc,
                        inputAmount * (1.toDouble() / selectCurrencyRate) * entry.value
                    )
                }

                val items = mutableListOf<MainAdapter.Item>()
                    .also { items ->

                        if (currencyItem != null) {
                            items.add(currencyItem)
                        }

                        if (!currencyRateItem.isNullOrEmpty()) {
                            items.addAll(currencyRateItem)
                        }
                    }
                items
            })
            .compose(RxUtil.applyIoMainSchedulers())
            .doOnSubscribe {
                _progressVisibility.postValue(true)
            }
            .doFinally {
                _progressVisibility.postValue(false)
            }
            .subscribe({
                _currencyItems.postValue(it)
            }, {
                Log.e(TAG, it.toString())
            })
        addDisposable(disposable)
    }
}