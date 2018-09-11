package com.example.pavelsvetlugins.currencyexchange.Fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.pavelsvetlugins.currencyexchange.CountryAdapter
import com.example.pavelsvetlugins.currencyexchange.CurrencyDetails
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CountryDataLoad
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CountryLoadListener
import com.example.pavelsvetlugins.currencyexchange.R
import com.example.pavelsvetlugins.currencyexchange.R.layout.country_view
import com.example.pavelsvetlugins.currencyexchange.SharedViewModel
import kotlinx.android.synthetic.main.country_view.*


interface GetCountryData{
    fun loadCountryListInterface(listener: CountryLoadListener)
}

open class CountryFragment : Fragment(), CountryAdapter.Listener, CountryLoadListener {

    val data: GetCountryData = CountryDataLoad()

    private var mAdapter: CountryAdapter? = null

    val TAG = CountryFragment::class.java.simpleName!!

    private lateinit var fm: FragmentManager

    private lateinit var model: SharedViewModel

    private val currencyFragment = CurrencyFragment()

    private val countryDataLoad = CountryDataLoad()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(country_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        country_header.visibility = View.GONE
        rv_country_list.visibility = View.GONE

        country_loading_layout.visibility = View.VISIBLE

        model = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        fm = fragmentManager!!

        country_retry_btn.setOnClickListener {
            retryLoadData()
        }

        initRecyclerView()

        if (model.countryList != null) {
            country_loading_layout.visibility = View.GONE
            rv_country_list.visibility = View.VISIBLE
            country_header.visibility = View.VISIBLE
            Log.v(TAG, "model country list is not null")
            mAdapter = CountryAdapter(ArrayList(model.countryList), this@CountryFragment)
            rv_country_list.adapter = mAdapter
        } else {
            loadCountryList()
        }
    }

    private fun initRecyclerView() {
        rv_country_list.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        rv_country_list.layoutManager = layoutManager
    }


    private fun loadCountryList() {
        countryDataLoad.loadCountryList(this)
    }


    override fun success(response: ArrayList<CurrencyDetails>) {
        rv_country_list.visibility = View.VISIBLE
        country_header.visibility = View.VISIBLE
        country_loading_layout.visibility = View.GONE

        Log.v(TAG, "Respons succesful")
        model.countryList = response
        mAdapter = CountryAdapter(response, this@CountryFragment)
        rv_country_list.adapter = mAdapter
    }


    override fun failed(message: String) {
        country_on_failure_view.visibility = View.VISIBLE
        country_loading_layout.visibility = View.GONE
    }

    private fun retryLoadData() {
        country_on_failure_view.visibility = View.GONE
        country_loading_layout.visibility = View.VISIBLE
        loadCountryList()
    }


    override fun onItemClick(currencyDetails: CurrencyDetails) {
        Toast.makeText(activity, "${currencyDetails.name} Clicked !", Toast.LENGTH_LONG).show()
        model.currencyDetailsModel = currencyDetails

        val transaction = fm.beginTransaction()
        transaction.replace(R.id.container, currencyFragment, currencyFragment.TAG)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}