package com.chrischen.currencyconversion.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by chris chen on 2019-10-01.
 */

open class BaseViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    fun addDisposable(vararg disposables: Disposable) {
        disposables.forEach {
            compositeDisposable.addAll(it)
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}