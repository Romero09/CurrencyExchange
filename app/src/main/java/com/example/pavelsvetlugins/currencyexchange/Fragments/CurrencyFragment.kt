package com.example.pavelsvetlugins.currencyexchange.Fragments

import android.app.ProgressDialog
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
import com.google.gson.*
import kotlinx.android.synthetic.main.currency_view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*



open class CurrencyFragment: Fragment(), CurrencyAdapter.Listener {

    private val BASE_URL = "http://data.fixer.io"

    private var mCurrencyRateList: ArrayList<LocalCurrency>? = null

    private val TAG = CurrencyFragment::class.java.simpleName

    private var mAdapter: CurrencyAdapter? = null

    private var isLoading: Boolean = false

    private lateinit var model: SharedViewModel

    private var selectedCurrency: CurrencyDetails? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.currency_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)


        initRecyclerView()

    }

    fun onActivitySwitch(){
        if(!isLoading) {
            DisplayProgressDialog()
        }
        selectedCurrency = model.currencyDetailsModel
        mAdapter?.clear()
        rv_rate_list.adapter = mAdapter
        loadJSON()
        isLoading = true
    }


    private fun initRecyclerView() {

        rv_rate_list.setHasFixedSize(true)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
        rv_rate_list.layoutManager = layoutManager
    }



    class CurrencyListDeserializer : JsonDeserializer<Rates> {

        @Throws(JsonParseException::class)
        override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Rates {
            Log.v("Compiles tag RATES", "it compiles")
            val jsonObject = element.asJsonObject.get("rates").asJsonObject
            Log.v("some object RATES", element.toString())
            Log.v("object RATES", jsonObject.toString())
            val countryList = ArrayList<LocalCurrency>()
            for ((key , value) in jsonObject.entrySet()) {
                // For individual City objects, we can use default deserialisation:
                val currency = key
                val country = context.deserialize<Double>(value, Double::class.java)
                Log.v("KEY RATES", key)
                Log.v("deserialize RATES", country.toString())
                val rate = LocalCurrency(key, context.deserialize(value, Double::class.java))
                countryList.add(rate)
            }
            Log.v("Country List", countryList.toString())
            return Rates(countryList)
        }
    }

    private fun loadJSON() {
        val builder = GsonBuilder()
        builder.registerTypeAdapter(Rates::class.java, CurrencyListDeserializer())
        val gson = builder.create()

        val requestInterface = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(CurrencyConverterApi::class.java)

        val call = requestInterface.getCurrency()
        Log.d("REQUEST  RATES", call.toString() + "")


        call.enqueue(object : Callback<Rates> {
            override fun onResponse(call: Call<Rates>, response: retrofit2.Response<Rates>?) {
                if (response != null) {
                    if (pDialog != null && pDialog!!.isShowing()) {
                        pDialog.dismiss()
                    }

                    val list = response.body()!!
                    Log.d("RESPONSE", "" + list.toString())

                    mCurrencyRateList = ArrayList(list.currency)
                    mAdapter = CurrencyAdapter(mCurrencyRateList!!, this@CurrencyFragment)
                    rv_rate_list.adapter = mAdapter
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<Rates>, t: Throwable) {
                Log.d(TAG, t.localizedMessage)
                Toast.makeText(activity, "Error ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onItemClick(localCurrency: LocalCurrency) {
        Toast.makeText(activity, "${localCurrency.currency} ${localCurrency.rate} Clicked !", Toast.LENGTH_LONG).show()

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