package com.example.pavelsvetlugins.currencyexchange

import android.arch.lifecycle.ViewModel
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CountryDataLoad
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CountryFetchData
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CurrencyDataLoad
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CurrencyFetchData


class SharedViewModel : ViewModel() {
    var currencyDetailsModel: CurrencyDetails? = null
}
