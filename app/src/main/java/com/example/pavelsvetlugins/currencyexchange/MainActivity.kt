@file:Suppress("DEPRECATION")

package com.example.pavelsvetlugins.currencyexchange

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.pavelsvetlugins.currencyexchange.Fragments.CountryFragment


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName

    var app: MyApplication? = null

    private var manager = supportFragmentManager
    private var transaction = manager.beginTransaction()

    private val countryFragment = CountryFragment()

    private lateinit var model: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        app = applicationContext as MyApplication
        model = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        transaction.add(R.id.container, countryFragment, countryFragment.TAG)
        transaction.commit()

    }


    override fun onBackPressed() {

        if (app?.countryDataFetching != null) {
            app?.countryDataFetching?.countryFetchCancel()
            countryFragment.isExit = true
        }
        app?.currencyDataFetch?.currencyFetchCancel()
        super.onBackPressed()
    }

}