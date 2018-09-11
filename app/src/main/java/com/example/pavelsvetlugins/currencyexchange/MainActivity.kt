@file:Suppress("DEPRECATION")

package com.example.pavelsvetlugins.currencyexchange

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CountryDataLoad
import com.example.pavelsvetlugins.currencyexchange.Fragments.CountryFragment
import com.example.pavelsvetlugins.currencyexchange.Fragments.CurrencyFragment
import android.R.attr.fragment
import android.util.Log


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName

    private var manager = supportFragmentManager
    private var transaction = manager.beginTransaction()

    val countryFragment = CountryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        transaction.add(R.id.container, countryFragment, countryFragment.TAG)
        transaction.commit()

    }




















}