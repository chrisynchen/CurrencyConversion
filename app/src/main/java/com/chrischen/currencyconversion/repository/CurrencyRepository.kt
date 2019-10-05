package com.chrischen.currencyconversion.repository

import com.chrischen.currencyconversion.BuildConfig
import com.chrischen.currencyconversion.network.request.CurrencyService
import com.chrischen.currencyconversion.network.response.CurrencyListDetail
import com.chrischen.currencyconversion.network.response.ExchangeRate
import com.chrischen.currencyconversion.storage.CurrencyPreference
import io.reactivex.Single
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by chris chen on 2019-10-01.
 */
class CurrencyRepository @Inject constructor(
    private val currencyService: CurrencyService
) : ICurrencyRepository {

    private val currencyListDetailCacheTime = TimeUnit.HOURS.toMillis(1)
    private val exchangeRateCacheTime = TimeUnit.MINUTES.toMillis(1)

    override fun fetchCurrencyList(): Single<Response<CurrencyListDetail?>> {

        val currentTime = System.currentTimeMillis()

        if (currentTime - CurrencyPreference.currencyListDetailTimestamp < currencyListDetailCacheTime) {
            return Single.just(Response.success(CurrencyPreference.currencyListDetail))
        }

        return currencyService.fetchCurrencyList(BuildConfig.API_ACCESS_KEY)
            .doOnSuccess {
                CurrencyPreference.currencyListDetailTimestamp = currentTime
                CurrencyPreference.currencyListDetail = it.body()
            }
    }

    override fun fetchRecentExchangeRate(): Single<Response<ExchangeRate?>> {

        val currentTime = System.currentTimeMillis()

        if (currentTime - CurrencyPreference.exchangeRateTimestamp < exchangeRateCacheTime) {
            return Single.just(Response.success(CurrencyPreference.exchangeRate))
        }

        return currencyService.fetchRecentExchangeRate(BuildConfig.API_ACCESS_KEY)
            .doOnSuccess {
                CurrencyPreference.exchangeRateTimestamp = currentTime
                CurrencyPreference.exchangeRate = it.body()
            }
    }
}