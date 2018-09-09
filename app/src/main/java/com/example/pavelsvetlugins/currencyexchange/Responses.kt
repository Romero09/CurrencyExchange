package com.example.pavelsvetlugins.currencyexchange

data class Response(val results: ResponseCountryList)

data class ResponseCountryList(val currencyContainer: List<CurrencyDetails>)

data class CurrencyDetails(val alpha3: String,
                           val currencyId: String,
                           val currencyName: String,
                           val currencySymbol: String,
                           val id: String,
                           val name: String)

