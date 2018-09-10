package com.example.pavelsvetlugins.currencyexchange

import android.arch.lifecycle.ViewModel


class SharedViewModel : ViewModel() {
    var currencyDetailsModel: CurrencyDetails? = null
    var countryList: ArrayList<CurrencyDetails>? = null
}
