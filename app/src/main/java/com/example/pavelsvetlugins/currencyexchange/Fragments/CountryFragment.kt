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
import com.example.pavelsvetlugins.currencyexchange.*
import com.example.pavelsvetlugins.currencyexchange.R.layout.country_view
import com.google.gson.*
import kotlinx.android.synthetic.main.country_view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*







open class CountryFragment: Fragment(), CountryAdapter.Listener {

    private val BASE_URL = "https://free.currencyconverterapi.com"

    private var mCurrencyDetailsList: ArrayList<CurrencyDetails>? = null

    private var mAdapter: CountryAdapter? = null

    val TAG = CountryFragment::class.java.simpleName

    private lateinit var fm: FragmentManager

    private lateinit var model: SharedViewModel

    val currencyFragment = CurrencyFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(country_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        initRecyclerView()
        DisplayProgressDialog()
        loadJSON()
        fm = fragmentManager!!
    }

    private fun initRecyclerView() {

        rv_android_list.setHasFixedSize(true)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(activity)
        rv_android_list.layoutManager = layoutManager
    }


     class CountryListDeserializer : JsonDeserializer<Response> {

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
        builder.registerTypeAdapter(Response::class.java, CountryFragment.CountryListDeserializer())
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

                    mCurrencyDetailsList = ArrayList((list.results.currencyContainer).sortedWith(compareBy{ it.name }))
                    mAdapter = CountryAdapter(mCurrencyDetailsList!!, this@CountryFragment)
                    rv_android_list.adapter = mAdapter
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Log.d(TAG, t.localizedMessage)
                Toast.makeText(activity, "Error ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
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