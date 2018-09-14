package com.example.pavelsvetlugins.currencyexchange

import java.io.Serializable

data class Response(val results: ResponseCountryList)

data class ResponseCountryList(val countryContainer: List<CountryDetails>)

data class CountryDetails(val alpha3: String,
                          val currencyId: String,
                          val currencyName: String,
                          val currencySymbol: String,
                          val id: String,
                          val name: String): Serializable

