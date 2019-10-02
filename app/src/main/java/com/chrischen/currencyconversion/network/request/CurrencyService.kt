package com.chrischen.currencyconversion.network.request

import com.chrischen.currencyconversion.network.response.CurrencyListDetail
import com.chrischen.currencyconversion.network.response.ExchangeRate
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by chris chen on 2019-10-01.
 */
interface CurrencyService {

    @GET("live")
    fun fetchRecentExchangeRate(@Query("access_key") key: String): Single<Response<ExchangeRate>>

    @GET("list")
    fun fetchCurrencyList(@Query("access_key") key: String): Single<Response<CurrencyListDetail>>
}