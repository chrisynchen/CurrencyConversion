package com.chrischen.currencyconversion

import android.app.Application
import com.chrischen.currencyconversion.storage.CurrencyPreference

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CurrencyPreference.init(this)
    }
}