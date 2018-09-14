package com.example.pavelsvetlugins.currencyexchange

import java.io.Serializable

data class Rates(val currency: List<LocalCurrency>)

data class LocalCurrency(val currency: String, val rate: Double): Serializable