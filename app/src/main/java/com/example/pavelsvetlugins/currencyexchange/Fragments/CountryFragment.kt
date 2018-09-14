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
import com.example.pavelsvetlugins.currencyexchange.*
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CountryFetchData
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CountryLoadListener
import com.example.pavelsvetlugins.currencyexchange.R.layout.country_view
import kotlinx.android.synthetic.main.country_view.*


open class CountryFragment : Fragment(), CountryLoadListener {

    var app: MyApplication? = null

    var isExit = false

    var countryDataFetching: CountryFetchData? = null

    private var mAdapter: CountryAdapter? = null

    val TAG = CountryFragment::class.java.simpleName!!

    private lateinit var fm: FragmentManager

    private lateinit var sharedViewModel: SharedViewModel

    private val currencyFragment = CurrencyFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(country_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        country_header.visibility = View.GONE
        rv_country_list.visibility = View.GONE

        app = activity?.getApplicationContext() as MyApplication
        countryDataFetching = app?.countryDataFetching!!

        country_loading_layout.visibility = View.VISIBLE

        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        fm = fragmentManager!!

        country_retry_btn.setOnClickListener {
            retryLoadData()
        }

        //Initializing recycler view
        initRecyclerView()

        loadCountryList()
    }

    private fun initRecyclerView() {
        rv_country_list.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        rv_country_list.layoutManager = layoutManager
    }


    private fun loadCountryList() {
        countryDataFetching?.loadCountryList(this)
    }


    override fun success(response: ArrayList<CountryDetails>) {
        rv_country_list.visibility = View.VISIBLE
        country_header.visibility = View.VISIBLE
        country_loading_layout.visibility = View.GONE

        Log.v(TAG, "Response successful")
        mAdapter = CountryAdapter(response) { onItemClick(it) }

        rv_country_list.adapter = mAdapter
    }


    override fun failed(message: String) {
        if (isExit) {
            return
        }
        country_on_failure_view.visibility = View.VISIBLE
        country_loading_layout.visibility = View.GONE
    }

    private fun retryLoadData() {
        country_on_failure_view.visibility = View.GONE
        country_loading_layout.visibility = View.VISIBLE
        loadCountryList()
    }


    private fun onItemClick(countryDetails: CountryDetails) {
        sharedViewModel.countryDetailsModel = countryDetails
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.container, currencyFragment, currencyFragment.TAG)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}