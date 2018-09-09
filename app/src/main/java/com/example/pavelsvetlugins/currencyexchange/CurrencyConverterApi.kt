package com.example.pavelsvetlugins.currencyexchange

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET

interface CurrencyConverterApi{
    @GET("/api/v6/countries")
    fun getCountries(): Call<Response>

    @GET("/api/latest?access_key=8f8e8afc75c021487bbfab04bdf1627e")
    fun getCurrency(): Call<Rates>
}