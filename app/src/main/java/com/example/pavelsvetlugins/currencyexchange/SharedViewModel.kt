package com.example.pavelsvetlugins.currencyexchange

import android.arch.lifecycle.ViewModel
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CountryDataLoad
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CountryFetchData
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CurrencyDataLoad
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CurrencyFetchData


class SharedViewModel : ViewModel() {
    var currencyDetailsModel: CurrencyDetails? = null
    var countryList: ArrayList<CurrencyDetails>? = null
    var currencyList: ArrayList<LocalCurrency>? = null
    var currencyDataLoadInstance: CurrencyFetchData? = null
    var countryDataLoadInstance: CountryFetchData? = null
}
