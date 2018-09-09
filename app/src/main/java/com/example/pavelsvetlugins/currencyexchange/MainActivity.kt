@file:Suppress("DEPRECATION")

package com.example.pavelsvetlugins.currencyexchange

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.google.gson.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*


class MainActivity : AppCompatActivity(), CountryAdapter.Listener {


    private val TAG = MainActivity::class.java.simpleName

    private val BASE_URL = "https://free.currencyconverterapi.com"

    private var mAndroidArrayList: ArrayList<CurrencyDetails>? = null

    private var mAdapter: CountryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()
        DisplayProgressDialog()
        loadJSON()

    }

    private fun initRecyclerView() {

        rv_android_list.setHasFixedSize(true)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        rv_android_list.layoutManager = layoutManager
    }


    inner class CountryListDeserializer : JsonDeserializer<Response> {

        @Throws(JsonParseException::class)
        override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Response {
            Log.v("Compiles tag", "it compiles")
            val jsonObject = element.asJsonObject.get("results").asJsonObject
            Log.v("some object", element.toString())
            Log.v("object", jsonObject.toString())
            val countryList = ArrayList<CurrencyDetails>()
            for ((_, value) in jsonObject.entrySet()) {
                // For individual City objects, we can use default deserialisation:
                val city = context.deserialize<CurrencyDetails>(value, CurrencyDetails::class.java)
                countryList.add(city)
            }
            Log.v("Country List", countryList.toString())
            return Response(ResponseCountryList(countryList))
        }

    }

    private fun loadJSON() {

        val builder = GsonBuilder()
        builder.registerTypeAdapter(Response::class.java, CountryListDeserializer())
        val gson = builder.create()

        val requestInterface = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(CurrencyConverterApi::class.java)

        val call = requestInterface.getCountries()
        Log.d("REQUEST", call.toString() + "")


        call.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>?) {
                if (response != null) {
                    if (pDialog != null && pDialog!!.isShowing()) {
                        pDialog.dismiss()
                    }

                    val list = response.body()!!
                    Log.d("RESPONSE", "" + list.toString())

                    mAndroidArrayList = ArrayList(list.results.currencyContainer)
                    mAdapter = CountryAdapter(mAndroidArrayList!!, this@MainActivity)
                    rv_android_list.adapter = mAdapter
                    loadJSONCurrency()
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Log.d(TAG, t.localizedMessage)
                Toast.makeText(this@MainActivity, "Error ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onItemClick(currencyDetails: CurrencyDetails) {
        Toast.makeText(this, "${currencyDetails.name} Clicked !", Toast.LENGTH_LONG).show()

    }


    lateinit var pDialog: ProgressDialog
    fun DisplayProgressDialog() {

        pDialog = ProgressDialog(this@MainActivity)
        pDialog!!.setMessage("Loading..")
        pDialog!!.setCancelable(false)
        pDialog!!.isIndeterminate = false
        pDialog!!.show()
    }







    private val BASE_URL2 = "http://data.fixer.io"


    inner class CurrencyListDeserializer : JsonDeserializer<Rates> {

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



    private fun loadJSONCurrency() {

        val builder = GsonBuilder()
        builder.registerTypeAdapter(Rates::class.java, CurrencyListDeserializer())
        val gson = builder.create()

        val requestInterface = Retrofit.Builder()
                .baseUrl(BASE_URL2)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(CurrencyConverterApi::class.java)

        val call = requestInterface.getCurrency()
        Log.d("REQUEST  RATES", call.toString() + "")


        call.enqueue(object : Callback<Rates> {
            override fun onResponse(call: Call<Rates>, response: retrofit2.Response<Rates>?) {
                if (response != null) {
                    val list = response.body()!!
                    Log.d("RESPONSE RATES", "" + list.toString())

                }
            }

            override fun onFailure(call: Call<Rates>, t: Throwable) {
                Log.d(TAG, t.localizedMessage)
                Toast.makeText(this@MainActivity, "Error ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }





}