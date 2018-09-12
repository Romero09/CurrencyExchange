package com.example.pavelsvetlugins.currencyexchange

import android.app.Application
import android.content.Context
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.*

open class MyApplication(): Application(){

    var countryDataFetching: CountryFetchData? = null
    var currencyDataFetch: CurrencyFetchData? = null


    override fun onCreate() {

        countryDataFetching = CountryDataLoad()
        currencyDataFetch = CurrencyDataLoad()

        super.onCreate()
    }

}