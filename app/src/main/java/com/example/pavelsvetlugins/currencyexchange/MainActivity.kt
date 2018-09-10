@file:Suppress("DEPRECATION")

package com.example.pavelsvetlugins.currencyexchange

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.pavelsvetlugins.currencyexchange.Fragments.CountryFragment
import com.example.pavelsvetlugins.currencyexchange.Fragments.CurrencyFragment


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    var manager = supportFragmentManager
    var transaction = manager.beginTransaction()

    val countryFragment = CountryFragment()
    val currencyFragment = CurrencyFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        transaction.add(R.id.container, countryFragment, countryFragment.TAG)
        transaction.commit()


    }




















}