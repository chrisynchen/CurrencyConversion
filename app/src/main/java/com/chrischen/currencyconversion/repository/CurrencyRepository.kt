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

    private var currencyListDetailSingle: Single<Response<CurrencyListDetail>>? = null

    //TODO refactor to timestamp
    private var recentExchangeRate: Single<Response<ExchangeRate>>? = null

    override fun fetchCurrencyList(): Single<Response<CurrencyListDetail>> {
        // Currency list will not change frequently.
        // So we just call once while user try to fetch the currency list.
        val response = currencyListDetailSingle ?: currencyService.fetchCurrencyList(BuildConfig.API_ACCESS_KEY)
        currencyListDetailSingle = response

        return response
    }

    override fun fetchRecentExchangeRate(): Single<Response<ExchangeRate>> {

        //TODO refactor to timestamp
        val response = recentExchangeRate ?: currencyService.fetchRecentExchangeRate(BuildConfig.API_ACCESS_KEY)
        recentExchangeRate = response

        return response
    }
}