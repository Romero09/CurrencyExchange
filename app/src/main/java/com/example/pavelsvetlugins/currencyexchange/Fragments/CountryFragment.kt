package com.example.pavelsvetlugins.currencyexchange.Fragments

import android.app.ProgressDialog
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


open class CountryFragment : Fragment(), CountryAdapter.Listener, CountryLoadListener {


    private var mAdapter: CountryAdapter? = null

    val TAG = CountryFragment::class.java.simpleName

    private lateinit var fm: FragmentManager

    private lateinit var model: SharedViewModel

    val currencyFragment = CurrencyFragment()

    val countryDataLoad = CountryDataLoad()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(country_view, container, false)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        retry_btn.setOnClickListener {
            retryLoadData()
        }

        initRecyclerView()
        fm = fragmentManager!!


        if(model.countryList != null){
            Log.v(TAG, "model country list is not null")
            mAdapter = CountryAdapter(ArrayList(model.countryList), this@CountryFragment)
            rv_android_list.adapter = mAdapter
        } else {
            loadCountryList()
        }
    }

    private fun initRecyclerView() {

        rv_android_list.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        rv_android_list.layoutManager = layoutManager
    }


    private fun loadCountryList() {
        countryDataLoad.loadCountryList(this)
    }


    override fun success(response: ArrayList<CurrencyDetails>) {
        Log.v(TAG, "Respons succesful Status: ${countryDataLoad.status}")
        if (countryDataLoad.status == "Ok") {
            model.countryList = response
            mAdapter = CountryAdapter(response, this@CountryFragment)
            rv_android_list.adapter = mAdapter
        }
    }


    override fun failed(message: String) {
        on_failure_view.visibility = View.VISIBLE
        rv_android_list.visibility = View.GONE

    }

    private fun retryLoadData() {
        on_failure_view.visibility = View.GONE
        rv_android_list.visibility = View.VISIBLE
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