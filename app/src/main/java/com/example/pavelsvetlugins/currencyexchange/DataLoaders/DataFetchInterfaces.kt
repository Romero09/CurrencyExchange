package com.example.pavelsvetlugins.currencyexchange.DataLoaders

import android.content.Context
import android.util.LruCache
import com.example.pavelsvetlugins.currencyexchange.CurrencyDetails
import com.example.pavelsvetlugins.currencyexchange.LocalCurrency
import java.util.*

interface CountryFetchData {
    val mCountryMemoryCache: LruCache<String, ArrayList<CurrencyDetails>>
    val COUNTRY_URL: String
    fun loadCountryList(listener: CountryLoadListener)
    fun countryFetchCancel()
}

interface CurrencyFetchData {
    val mCurrencyMemoryCache: LruCache<String, Pair<Date, ArrayList<LocalCurrency>>>
    val CURRENCY_URL: String
    fun loadCurrencyList(listener: CurrencyLoadListener)
    fun currencyFetchCancel()
}

interface CurrencyLoadListener {
    fun success(response: ArrayList<LocalCurrency>)
    fun failed(message: String)
}

interface CountryLoadListener {
    fun success(response: ArrayList<CurrencyDetails>)
    fun failed(message: String)
}
