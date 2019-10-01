package com.chrischen.currencyconversion.dagger

import com.chrischen.currencyconversion.network.ApiManager
import com.chrischen.currencyconversion.network.request.CurrencyService
import dagger.Module
import dagger.Provides

/**
 * Created by chris chen on 2019-10-02.
 */
@Module
class NetworkModule {
    @Provides
    fun provideGithubService(): CurrencyService {
        return ApiManager.getInstance().create(CurrencyService::class.java)
    }
}