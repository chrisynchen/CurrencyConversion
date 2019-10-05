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
    private val currencyRepository: ICurrencyRepository =
        CurrencyRepository(currencyService, currencyPreference)

    @Before
    fun setUp() {
        viewModel = MainViewModel(currencyRepository)
    }

    @Test
    fun onRefresh_fetchCurrencyList_failed() {
        // Given
        whenever(currencyService.fetchCurrencyList(anyString()))
            .thenReturn(Single.error(Throwable("onRefresh_fetchCurrencyList_failed")))
        whenever(currencyService.fetchRecentExchangeRate(anyString()))
            .thenReturn(Single.just(Response.success(ExchangeRate("", LinkedHashMap()))))

        // When
        viewModel.onRefresh()

        // Then
        assertEquals(false, viewModel.progressVisibility.value)
        assertEquals("onRefresh_fetchCurrencyList_failed", viewModel.logMessage.value?.second)
        assertEquals("onRefresh_fetchCurrencyList_failed", viewModel.toastMessage.value)
    }

    @Test
    fun onRefresh_fetchRecentExchangeRate_failed() {
        // Given
        whenever(currencyService.fetchRecentExchangeRate(anyString()))
            .thenReturn(Single.error(Throwable("onRefresh_fetchRecentExchangeRate_failed")))
        whenever(currencyService.fetchCurrencyList(anyString()))
            .thenReturn(Single.just(Response.success(CurrencyListDetail(LinkedHashMap()))))

        // When
        viewModel.onRefresh()

        // Then
        assertEquals(false, viewModel.progressVisibility.value)
        assertEquals("onRefresh_fetchRecentExchangeRate_failed", viewModel.logMessage.value?.second)
        assertEquals("onRefresh_fetchRecentExchangeRate_failed", viewModel.toastMessage.value)
    }

    @Test
    fun onRefresh_success() {
        // Given
        val currencyListMap = LinkedHashMap<String, String>()
            .also {
                it["USD"] = "this is USD"
            }
        val exchangeRateListMap = LinkedHashMap<String, Double>()
            .also {
                it["USDUSD"] = 1.toDouble()
            }
        val currencyListDetail = CurrencyListDetail(currencyListMap)
        val exchangeRate = ExchangeRate("", exchangeRateListMap)
        whenever(currencyService.fetchRecentExchangeRate(anyString()))
            .thenReturn(Single.just(Response.success(exchangeRate)))
        whenever(currencyService.fetchCurrencyList(anyString()))
            .thenReturn(Single.just(Response.success(currencyListDetail)))

        // When
        viewModel.onRefresh()

        // Then
        assertEquals(2, viewModel.currencyItems.value?.size)
        assertEquals(false, viewModel.progressVisibility.value)
    }

    @Test
    fun changeCurrency_null() {
        val currencyListMap = LinkedHashMap<String, String>()
            .also {
                it["USD"] = "this is USD"
            }
        val exchangeRateListMap = LinkedHashMap<String, Double>()
            .also {
                it["USDUSD"] = 1.toDouble()
            }
        val currencyListDetail = CurrencyListDetail(currencyListMap)
        val exchangeRate = ExchangeRate("", exchangeRateListMap)


        whenever(currencyService.fetchRecentExchangeRate(anyString()))
            .thenReturn(Single.just(Response.success(exchangeRate)))
        whenever(currencyService.fetchCurrencyList(anyString()))
            .thenReturn(Single.just(Response.success(currencyListDetail)))

        viewModel.changeCurrency(null)
        assertEquals(2, viewModel.currencyItems.value?.size)
        assertEquals(false, viewModel.progressVisibility.value)
    }

    @Test
    fun changeCurrency_success() {
        val currencyListMap = LinkedHashMap<String, String>()
            .also {
                it["USD"] = "this is USD"
                it["JPD"] = "this is JPD"
            }
        val exchangeRateListMap = LinkedHashMap<String, Double>()
            .also {
                it["USDUSD"] = 1.toDouble()
                it["USDJPD"] = 100.toDouble()
            }
        val currencyListDetail = CurrencyListDetail(currencyListMap)
        val exchangeRate = ExchangeRate("", exchangeRateListMap)


        whenever(currencyService.fetchRecentExchangeRate(anyString()))
            .thenReturn(Single.just(Response.success(exchangeRate)))
        whenever(currencyService.fetchCurrencyList(anyString()))
            .thenReturn(Single.just(Response.success(currencyListDetail)))

        viewModel.changeCurrency("JPD")
        assertEquals(3, viewModel.currencyItems.value?.size)
        assertEquals(false, viewModel.progressVisibility.value)
        assertEquals(null, viewModel.logMessage.value?.second)
        assertEquals(null, viewModel.toastMessage.value)
    }
}