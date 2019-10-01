package com.chrischen.currencyconversion.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chrischen.currencyconversion.repository.ICurrencyRepository
import com.chrischen.currencyconversion.utility.RxUtil

/**
 * Created by chris chen on 2019-10-02.
 */

class MainViewModel(private val currencyRepository: ICurrencyRepository) : BaseViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean>
        get() = _progressVisibility

    fun onRefresh() {
        val disposable = currencyRepository.fetchRecentExchangeRate()
            .compose(RxUtil.applyIoMainSchedulers())
            .doOnSubscribe {
                _progressVisibility.postValue(true)
            }
            .doFinally {
                _progressVisibility.postValue(false)
            }
            .subscribe({

            }, {
                Log.e(TAG, it.toString())
            })
        addDisposable(disposable)
    }
}