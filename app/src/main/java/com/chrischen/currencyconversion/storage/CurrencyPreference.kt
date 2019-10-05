package com.chrischen.currencyconversion.storage

import android.content.Context
import android.content.SharedPreferences
import com.chrischen.currencyconversion.network.response.CurrencyListDetail
import com.chrischen.currencyconversion.network.response.ExchangeRate
import com.google.gson.GsonBuilder

object CurrencyPreference {
    private const val PREFERENCES_NAME = "currency_preferences"
    private const val PREFERENCES_MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private const val CURRENCY_LIST_DETAIL = "CURRENCY_LIST_DETAIL"
    private const val CURRENCY_LIST_DETAIL_TIMESTAMP = "CURRENCY_LIST_DETAIL_TIMESTAMP"
    private const val EXCHANGE_RATE = "EXCHANGE_RATE"
    private const val EXCHANGE_RATE_TIMESTAMP = "EXCHANGE_RATE_TIMESTAMP"


    fun init(context: Context) {
        preferences =
            context.applicationContext.getSharedPreferences(PREFERENCES_NAME, PREFERENCES_MODE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var currencyListDetail: CurrencyListDetail?
        get() = try {
            val currencyListDetail = GsonBuilder().create().fromJson(
                preferences.getString(CURRENCY_LIST_DETAIL, null),
                CurrencyListDetail::class.java
            )
            currencyListDetail
        } catch (e: Exception) {
            null
        }
        set(value) = preferences.edit {
            if (value == null) {
                it.putString(CURRENCY_LIST_DETAIL, value)
                return@edit
            }
            var currencyListDetail: String? = null
            try {
                currencyListDetail = GsonBuilder().create().toJson(value)
            } catch (exception: Exception) {
                //do nothing
            }
            it.putString(CURRENCY_LIST_DETAIL, currencyListDetail)
        }

    var exchangeRate: ExchangeRate?
        get() = try {
            val exchangeRate = GsonBuilder().create().fromJson(
                preferences.getString(EXCHANGE_RATE, null),
                ExchangeRate::class.java
            )
            exchangeRate
        } catch (e: Exception) {
            null
        }
        set(value) = preferences.edit {
            if (value == null) {
                it.putString(EXCHANGE_RATE, value)
                return@edit
            }
            var exchangeRate: String? = null
            try {
                exchangeRate = GsonBuilder().create().toJson(value)
            } catch (exception: Exception) {
                //do nothing
            }
            it.putString(EXCHANGE_RATE, exchangeRate)
        }

    var currencyListDetailTimestamp: Long
        get() = preferences.getLong(CURRENCY_LIST_DETAIL_TIMESTAMP, 0)
        set(value) = preferences.edit {
            it.putLong(CURRENCY_LIST_DETAIL_TIMESTAMP, value)
        }

    var exchangeRateTimestamp: Long
        get() = preferences.getLong(EXCHANGE_RATE_TIMESTAMP, 0)
        set(value) = preferences.edit {
            it.putLong(EXCHANGE_RATE_TIMESTAMP, value)
        }
}