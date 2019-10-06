package com.chrischen.currencyconversion.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chrischen.currencyconversion.adapter.MainAdapter
import com.chrischen.currencyconversion.network.response.CurrencyListDetail
import com.chrischen.currencyconversion.network.response.ExchangeRate
import com.chrischen.currencyconversion.repository.ICurrencyRepository
import com.chrischen.currencyconversion.utility.RxUtil
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import retrofit2.Response
import java.util.concurrent.TimeUnit

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

    private val _logMessage = MutableLiveData<Pair<String, String>>()
    val logMessage: LiveData<Pair<String, String>>
        get() = _logMessage

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private var selectCurrency = ""

    private var inputAmount = 0.toDouble()

    private val publishSubject = PublishSubject.create<String>()

    init {
        val disposable = publishSubject
            .debounce(1, TimeUnit.SECONDS)
            .map { amountString ->
                var amount = 0.toDouble()
                try {
                    if (amountString.isNotEmpty()) {
                        amount = amountString.toDouble()
                    }
                } catch (e: NumberFormatException) {
                    _logMessage.postValue(Pair(TAG, e.message ?: ""))
                }

                amount
            }
            .compose(RxUtil.applyIoMainObservableSchedulers())
            .subscribe(
                { amount ->
                    onAmountOrCurrencyChanged(inputAmount = amount)
                },
                {
                    _logMessage.postValue(Pair(TAG, it.message ?: ""))
                    _toastMessage.postValue(it.message ?: "")
                })
        addDisposable(disposable)
    }

    fun onRefresh() {
        onAmountOrCurrencyChanged()
    }

    fun changeAmount(amountText: CharSequence?) {
        publishSubject.onNext(amountText?.toString() ?: 0.toString())
    }

    fun changeCurrency(currency: String?) {
        onAmountOrCurrencyChanged(currency = currency ?: "")
    }

    private fun onAmountOrCurrencyChanged(
        currency: String = this.selectCurrency,
        inputAmount: Double = this.inputAmount
    ) {
        selectCurrency = currency
        this.inputAmount = inputAmount

        val disposable = Single.zip(
            currencyRepository.fetchCurrencyList(),
            currencyRepository.fetchRecentExchangeRate(),
            BiFunction<Response<CurrencyListDetail?>, Response<ExchangeRate?>, List<MainAdapter.Item>> { currencyListDetailResponse, exchangeRateResponse ->

                val currencyList =
                    currencyListDetailResponse.body()?.currencies?.keys?.toList() ?: emptyList()

                //Only happened when user not select currency. use default source instead.
                if (selectCurrency.isEmpty()) {
                    selectCurrency = exchangeRateResponse.body()?.source ?: ""
                }
                val topItem = if (currencyList.isEmpty()) {
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

                    //prevent Arithmeticexception
                    val currencyRate = if (selectCurrencyRate == 0.toDouble()) {
                        0.toDouble()
                    } else {
                        inputAmount * (entry.value / selectCurrencyRate)
                    }
                    MainAdapter.Item.CurrencyRateItem(
                        desc,
                        currencyRate
                    )
                }

                val items = mutableListOf<MainAdapter.Item>()
                    .also { items ->

                        if (topItem != null) {
                            items.add(topItem)
                        }

                        if (!currencyRateItem.isNullOrEmpty()) {
                            items.addAll(currencyRateItem)
                        }
                    }
                items
            })
            .compose(RxUtil.applyIoMainSingleSchedulers())
            .doOnSubscribe {
                _progressVisibility.postValue(true)
            }
            .doFinally {
                _progressVisibility.postValue(false)
            }
            .subscribe({
                _currencyItems.postValue(it)
            }, {
                _logMessage.postValue(Pair(TAG, it.message ?: ""))
                _toastMessage.postValue(it.message ?: "")
            })
        addDisposable(disposable)
    }
}