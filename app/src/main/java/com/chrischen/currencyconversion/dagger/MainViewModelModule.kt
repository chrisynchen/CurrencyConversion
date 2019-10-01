package com.chrischen.currencyconversion.dagger

import com.chrischen.currencyconversion.repository.CurrencyRepository
import com.chrischen.currencyconversion.viewmodel.MainViewModel
import dagger.Module
import dagger.Provides

/**
 * Created by chris chen on 2019-10-01.
 */
@Module
class MainViewModelModule {
    @Provides
    fun provideMainViewModel(currencyRepository: CurrencyRepository): MainViewModel = MainViewModel(currencyRepository)
}