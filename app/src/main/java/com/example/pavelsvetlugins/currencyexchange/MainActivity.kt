@file:Suppress("DEPRECATION")

package com.example.pavelsvetlugins.currencyexchange

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.pavelsvetlugins.currencyexchange.Fragments.CountryFragment


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName

    private var manager = supportFragmentManager
    private var transaction = manager.beginTransaction()

    private val countryFragment = CountryFragment()

    private lateinit var model: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        model = ViewModelProviders.of(this).get(SharedViewModel::class.java)


        transaction.add(R.id.container, countryFragment, countryFragment.TAG)
        transaction.commit()

    }


    override fun onBackPressed() {

        if (model.countryDataLoadInstance != null) {
            model.countryDataLoadInstance?.countryFetchCancel()
            countryFragment.isExit = true
        }
        model.currencyDataLoadInstance?.currencyFetchCancel()
        super.onBackPressed()
    }

}