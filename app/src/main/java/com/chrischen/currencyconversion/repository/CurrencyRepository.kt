package com.chrischen.currencyconversion.repository

import com.chrischen.currencyconversion.BuildConfig
import com.chrischen.currencyconversion.network.request.CurrencyService
import com.chrischen.currencyconversion.network.response.CurrencyListDetail
import com.chrischen.currencyconversion.network.response.ExchangeRate
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by chris chen on 2019-10-01.
 */
class CurrencyRepository @Inject constructor(
    private val currencyService: CurrencyService
) : ICurrencyRepository {
    override fun fetchCurrencyList(): Single<Response<CurrencyListDetail?>> {
        return currencyService.fetchCurrencyList(BuildConfig.API_ACCESS_KEY)
    }

    override fun fetchRecentExchangeRate(): Single<Response<ExchangeRate?>> {
        return currencyService.fetchRecentExchangeRate(BuildConfig.API_ACCESS_KEY)
    }
}