package com.chrischen.currencyconversion.network

import com.chrischen.currencyconversion.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by chris chen on 2019-10-01.
 */
class ApiManager private constructor(
    builder: OkHttpClient.Builder = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
) {

    init {
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(builder.build())
        .build()

    companion object {

        private val apiManagerInstance: ApiManager by lazy {
            ApiManager()
        }

        fun getInstance(): ApiManager {
            return apiManagerInstance
        }
    }

    fun <T> create(service: Class<T>): T {
        return apiManagerInstance.retrofit.create(service)
    }
}