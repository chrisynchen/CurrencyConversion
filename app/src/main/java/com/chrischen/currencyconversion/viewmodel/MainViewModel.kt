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

    private val _currencyRateItems = MutableLiveData<List<MainAdapter.CurrencyRateItem>>()
    val currencyRateItems: LiveData<List<MainAdapter.CurrencyRateItem>>
        get() = _currencyRateItems

    private val _currencyList = MutableLiveData<List<String>>()
    val currencyList: LiveData<List<String>>
        get() = _currencyList

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean>
        get() = _progressVisibility

    fun onRefresh() {
        val disposable = Single.zip(
            currencyRepository.fetchCurrencyList(),
            currencyRepository.fetchRecentExchangeRate(),
            BiFunction<Response<CurrencyListDetail>, Response<ExchangeRate>, ExchangeRate> { currencyListDetailResponse, exchangeRateResponse ->
                currencyListDetailResponse.body()?.let {
                    _currencyList.postValue(it.currencies?.keys?.toList())
                }

                exchangeRateResponse.body()!!
            })
            .map {
                it.quotes?.map { entry ->
                    MainAdapter.CurrencyRateItem(entry.key, entry.value)
                }
            }
            .compose(RxUtil.applyIoMainSchedulers())
            .doOnSubscribe {
                _progressVisibility.postValue(true)
            }
            .doFinally {
                _progressVisibility.postValue(false)
            }
            .subscribe({
                _currencyRateItems.postValue(it)
            }, {
                Log.e(TAG, it.toString())
            })
        addDisposable(disposable)
    }
}