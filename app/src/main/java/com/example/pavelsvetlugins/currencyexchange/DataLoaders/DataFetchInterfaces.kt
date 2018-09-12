package com.example.pavelsvetlugins.currencyexchange.DataLoaders

import android.content.Context
import com.example.pavelsvetlugins.currencyexchange.CurrencyDetails
import com.example.pavelsvetlugins.currencyexchange.LocalCurrency
import java.util.ArrayList

interface CountryFetchData {
    fun loadCountryList(listener: CountryLoadListener)
    fun countryFetchCancel()
}

interface CurrencyFetchData {
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
