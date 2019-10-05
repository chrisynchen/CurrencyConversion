package com.chrischen.currencyconversion.dagger

import com.chrischen.currencyconversion.activity.MainActivity
import com.chrischen.currencyconversion.storage.ICurrencyPreference
import dagger.BindsInstance
import dagger.Component

/**
 * Created by chris chen on 2019-10-01.
 */
@Component(modules = [MainViewModelModule::class, NetworkModule::class])
interface MainComponent {
    fun inject(mainActivity: MainActivity)

    @Component.Builder
    interface Builder {
        fun build(): MainComponent
        @BindsInstance
        fun currencyPreference(currencyPreference: ICurrencyPreference): Builder
    }
}