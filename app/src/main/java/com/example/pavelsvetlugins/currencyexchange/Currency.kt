package com.example.pavelsvetlugins.currencyexchange

data class Rates(val currency: List<LocalCurrency>)

data class LocalCurrency(val currency: String, val rate: Double)