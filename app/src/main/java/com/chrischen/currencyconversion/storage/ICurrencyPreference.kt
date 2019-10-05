package com.chrischen.currencyconversion.storage

import android.content.Context
import com.chrischen.currencyconversion.network.response.CurrencyListDetail
import com.chrischen.currencyconversion.network.response.ExchangeRate

interface ICurrencyPreference {
    fun init(context: Context)

    var currencyListDetail: CurrencyListDetail?
    var exchangeRate: ExchangeRate?
    var currencyListDetailTimestamp: Long
    var exchangeRateTimestamp: Long
}