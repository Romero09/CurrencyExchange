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
import com.example.pavelsvetlugins.currencyexchange.R
import com.example.pavelsvetlugins.currencyexchange.R.layout.country_view
import com.example.pavelsvetlugins.currencyexchange.SharedViewModel
import kotlinx.android.synthetic.main.country_view.*







open class CountryFragment: Fragment(), CountryAdapter.Listener {


    private var mAdapter: CountryAdapter? = null

    val TAG = CountryFragment::class.java.simpleName

    private lateinit var fm: FragmentManager

    private lateinit var model: SharedViewModel

    val currencyFragment = CurrencyFragment()

    val countryDataLoad = CountryDataLoad()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(country_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        initRecyclerView()
        DisplayProgressDialog()
        fm = fragmentManager!!
        loadCountryList()
    }

    private fun initRecyclerView() {

        rv_android_list.setHasFixedSize(true)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
        rv_android_list.layoutManager = layoutManager
    }


    fun loadCountryList(){
        val result = countryDataLoad.loadCountryList()


        Log.v(TAG, "whats $result?")
        Log.v(TAG, countryDataLoad.status)


        if(countryDataLoad.status == "Ok") {
            if (pDialog != null && pDialog!!.isShowing()) {
                pDialog.dismiss()
            }
            mAdapter = CountryAdapter(model.countryList!!, this@CountryFragment)
            rv_android_list.adapter = mAdapter

        }
    }


    override fun onItemClick(currencyDetails: CurrencyDetails) {
        Toast.makeText(activity, "${currencyDetails.name} Clicked !", Toast.LENGTH_LONG).show()
        //(activity as MainActivity).setViewPager(1)
        model.currencyDetailsModel = currencyDetails

        val transaction = fm.beginTransaction()
        transaction.replace(R.id.container, currencyFragment, currencyFragment.TAG)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    lateinit var pDialog: ProgressDialog
    fun DisplayProgressDialog() {

        pDialog = ProgressDialog(activity)
        pDialog!!.setMessage("Loading..")
        pDialog!!.setCancelable(false)
        pDialog!!.isIndeterminate = false
        pDialog!!.show()
    }



}