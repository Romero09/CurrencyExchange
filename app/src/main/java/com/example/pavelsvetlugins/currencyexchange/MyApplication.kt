package com.example.pavelsvetlugins.currencyexchange

import android.app.Application
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.*

open class MyApplication(): Application(){


    var countryDataFetching: CountryFetchData? = null
    var currencyDataFetch: CurrencyFetchData? = null
    var diskCache: DiskCache? = null


    override fun onCreate() {

        countryDataFetching = CountryDataLoad(this)
        currencyDataFetch = CurrencyDataLoad(this)
        diskCache = DiskCache(this)

        super.onCreate()
    }

}