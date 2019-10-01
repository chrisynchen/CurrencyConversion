package com.chrischen.currencyconversion.repository

import com.chrischen.currencyconversion.network.response.ExchangeRate
import io.reactivex.Single
import retrofit2.Response

/**
 * Created by chris chen on 2019-10-01.
 */
interface ICurrencyRepository {
    fun fetchRecentExchangeRate(): Single<Response<ExchangeRate>>
}