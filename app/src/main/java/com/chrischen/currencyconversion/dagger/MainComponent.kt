package com.chrischen.currencyconversion.dagger

import com.chrischen.currencyconversion.activity.MainActivity
import dagger.Component

/**
 * Created by chris chen on 2019-10-01.
 */
@Component(modules = [MainViewModelModule::class, NetworkModule::class])
interface MainComponent {
    fun inject(mainActivity: MainActivity)
}