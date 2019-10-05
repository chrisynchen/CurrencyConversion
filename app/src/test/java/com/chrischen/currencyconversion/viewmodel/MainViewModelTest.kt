package com.chrischen.currencyconversion.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.chrischen.currencyconversion.RxImmediateSchedulerRule
import com.chrischen.currencyconversion.network.request.CurrencyService
import com.chrischen.currencyconversion.network.response.CurrencyListDetail
import com.chrischen.currencyconversion.network.response.ExchangeRate
import com.chrischen.currencyconversion.repository.CurrencyRepository
import com.chrischen.currencyconversion.repository.ICurrencyRepository
import com.chrischen.currencyconversion.storage.ICurrencyPreference
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.modules.junit4.PowerMockRunner
import retrofit2.Response
import kotlin.test.assertEquals

@RunWith(PowerMockRunner::class)
@PowerMockIgnore("javax.net.ssl.*")
class MainViewModelTest {

    @Rule
    var instantRule: TestRule = InstantTaskExecutorRule()

    @Rule
    var rxRule: TestRule = RxImmediateSchedulerRule()

    private lateinit var viewModel: MainViewModel

    private val currencyService: CurrencyService = mock()
    private val currencyPreference: ICurrencyPreference = mock()
    private val userRepository: ICurrencyRepository =
        CurrencyRepository(currencyService, currencyPreference)

    @Before
    fun setUp() {
        viewModel = MainViewModel(userRepository)
    }

    @Test
    fun onRefresh_fetchCurrencyList_failed() {
        // Given
        whenever(currencyService.fetchCurrencyList(anyString()))
            .thenReturn(Single.error(Throwable()))
        whenever(currencyService.fetchRecentExchangeRate(anyString()))
            .thenReturn(Single.just(Response.success(ExchangeRate("", LinkedHashMap()))))

        // When
        viewModel.onRefresh()

        // Then
        assertEquals(false, viewModel.progressVisibility.value)
    }

    @Test
    fun onRefresh_fetchRecentExchangeRate_failed() {
        // Given
        whenever(currencyService.fetchRecentExchangeRate(anyString()))
            .thenReturn(Single.error(Throwable()))
        whenever(currencyService.fetchCurrencyList(anyString()))
            .thenReturn(Single.just(Response.success(CurrencyListDetail(LinkedHashMap()))))

        // When
        viewModel.onRefresh()

        // Then
        assertEquals(false, viewModel.progressVisibility.value)
    }

    @Test
    fun subscribe_success() {
        // Given
    }
}