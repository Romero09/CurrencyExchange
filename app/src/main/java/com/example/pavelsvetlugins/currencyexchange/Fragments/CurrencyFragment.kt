package com.example.pavelsvetlugins.currencyexchange.Fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.pavelsvetlugins.currencyexchange.*
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CurrencyFetchData
import com.example.pavelsvetlugins.currencyexchange.DataLoaders.CurrencyLoadListener
import kotlinx.android.synthetic.main.currency_view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.example.pavelsvetlugins.currencyexchange.MyApplication


open class CurrencyFragment : Fragment(), CurrencyAdapter.Listener, CurrencyLoadListener {

    var app: MyApplication? = null

    val TAG = CurrencyFragment::class.java.simpleName

    private var mCurrencyRateList: ArrayList<LocalCurrency>? = null

    private var mAdapter: CurrencyAdapter? = null

    private lateinit var sharedViewModel: SharedViewModel

    private var selectedCurrency: CurrencyDetails? = null

    private var currencyDataFetch: CurrencyFetchData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.currency_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        app = activity?.getApplicationContext() as MyApplication
        currencyDataFetch = app?.currencyDataFetch!!

        currency_loading_layout.visibility = View.GONE

        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        selectedCurrency = sharedViewModel.currencyDetailsModel

        currency_loading_text.text = (currency_loading_text.text.toString() + " ${selectedCurrency?.name}")

        currency_retry_btn.setOnClickListener {
            retryLoadData()
        }

        updateHeader(selectedCurrency)
        initRecyclerView()

        val currencyCacheResult = currencyDataFetch?.mCurrencyMemoryCache?.get(currencyDataFetch?.CURRENCY_URL)


        if (currencyCacheResult != null && isUpToDate(currencyCacheResult)) {
                mCurrencyRateList = currencyCacheResult.second
                Log.v(TAG, "Getting currency list from cache")
                mAdapter = CurrencyAdapter(mCurrencyRateList!!, this@CurrencyFragment)
                rv_rate_list.adapter = mAdapter
        } else {
            loadCurrencyList()
        }
    }


    private fun initRecyclerView() {

        rv_rate_list.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        rv_rate_list.layoutManager = layoutManager
    }

    private fun loadCurrencyList() {
        currency_header.visibility = View.GONE
        rv_rate_list.visibility = View.GONE
        currency_loading_layout.visibility = View.VISIBLE
        currencyDataFetch?.loadCurrencyList(this)
    }

    override fun success(response: ArrayList<LocalCurrency>) {
        currency_loading_layout.visibility = View.GONE
        currency_header.visibility = View.VISIBLE
        rv_rate_list.visibility = View.VISIBLE
        mCurrencyRateList = ArrayList(rateCalculation(selectedCurrency, ArrayList(response)))
        mAdapter = CurrencyAdapter(mCurrencyRateList!!, this@CurrencyFragment)
        if (rv_rate_list != null) {
            rv_rate_list.adapter = mAdapter
        }

    }

    override fun failed(message: String) {
        if (currency_loading_layout != null) {
            currency_loading_layout.visibility = View.GONE
            currency_header.visibility = View.GONE
            rv_rate_list.visibility = View.GONE
            currency_on_failure_view.visibility = View.VISIBLE
        } else {
            return
        }
    }

    private fun retryLoadData() {
        currency_on_failure_view.visibility = View.GONE
        currency_loading_layout.visibility = View.VISIBLE
        loadCurrencyList()
    }


    override fun onItemClick(localCurrency: LocalCurrency) {
        Toast.makeText(activity, "${localCurrency.currency} ${("%.8f".format(localCurrency.rate))}", Toast.LENGTH_LONG).show()
    }

    private fun updateHeader(selectedCurrency: CurrencyDetails?) {

        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())

        currency_view_country_name.text = selectedCurrency?.name
        currency_view_currency_name.text = selectedCurrency?.currencyId
        currency_view_date.text = currentDate

    }


    private fun rateCalculation(currencyDetail: CurrencyDetails?, currencyRateList: ArrayList<LocalCurrency>): MutableList<LocalCurrency> {
        val changedRatesList = mutableListOf<LocalCurrency>()
        val selectedCurrency = currencyDetail?.currencyId
        var eurRateToSelected: Double = 0.0
        for (rate in currencyRateList) {
            if (rate.currency == selectedCurrency) {
                eurRateToSelected = 1 / rate.rate
            }
        }
        Log.v(TAG, "Selected currency $selectedCurrency EUR rate for selected currency $eurRateToSelected")
        for ((i, rate) in currencyRateList.withIndex()) {
            when (rate.currency) {
                "EUR" -> changedRatesList.add(LocalCurrency(rate.currency, eurRateToSelected))
                selectedCurrency -> null
                else -> changedRatesList.add(LocalCurrency(rate.currency, eurRateToSelected * rate.rate))
            }
        }
        Log.v(TAG, "List of calculated currencies $changedRatesList")
        return changedRatesList
    }

    private fun isUpToDate(currencyListPair: Pair<Date, java.util.ArrayList<LocalCurrency>>?): Boolean {
        if (currencyListPair != null) {
            val storedDate = currencyListPair.first
            val nowDate = Calendar.getInstance().time
            Log.v(TAG, "Stored date $storedDate now date $nowDate it is: ${(nowDate < storedDate)}")
            if (nowDate < storedDate) {
                return true
            }
        }
        return false

    }

}