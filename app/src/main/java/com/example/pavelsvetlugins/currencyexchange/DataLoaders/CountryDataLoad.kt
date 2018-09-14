package com.example.pavelsvetlugins.currencyexchange.DataLoaders

import android.content.Context
import android.util.Log
import android.util.LruCache
import com.example.pavelsvetlugins.currencyexchange.*
import com.google.gson.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type


class CountryDataLoad(val context: Context) : CountryFetchData {

    val TAG = CountryDataLoad::class.java.simpleName

    var responseResult: ArrayList<CountryDetails>? = null

    var call: Call<Response>? = null

    val cacheSize = (Runtime.getRuntime().maxMemory().toInt()) / 8

    override val mCountryMemoryCache = LruCache<String, ArrayList<CountryDetails>>(cacheSize)

    var app = context.getApplicationContext() as MyApplication

    override val COUNTRY_URL = "https://free.currencyconverterapi.com"


    class CountryListDeserializer : JsonDeserializer<Response> {

        @Throws(JsonParseException::class)
        override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Response {
            val jsonObject = element.asJsonObject.get("results").asJsonObject
            Log.v("some object", element.toString())
            Log.v("object", jsonObject.toString())
            val countryList = ArrayList<CountryDetails>()
            for ((_, value) in jsonObject.entrySet()) {
                // For individual Country objects, we can use default deserialisation:
                val city = context.deserialize<CountryDetails>(value, CountryDetails::class.java)
                countryList.add(city)
            }
            Log.v("Country List", countryList.toString())
            return Response(ResponseCountryList(countryList))
        }
    }


    override fun loadCountryList(listener: CountryLoadListener) {
        Log.v(TAG, "Cache size $cacheSize")
        val diskCacheResult = app.diskCache?.readCountryListFromDiskCache(COUNTRY_URL)
        val cacheResult = mCountryMemoryCache.get(COUNTRY_URL)
        if (diskCacheResult == null) {

            val cacheListener = object : CountryLoadListener {
                override fun success(response: java.util.ArrayList<CountryDetails>) {

                    app.diskCache?.writeCountryListToDiskCache(COUNTRY_URL, responseResult!!)
                    Log.v(TAG, "DiskCache was null, creating new COUNTRY_URL cache")
                    if (cacheResult == null) {
                        mCountryMemoryCache.put(COUNTRY_URL, responseResult)
                        Log.v(TAG, "Cache was null, creating new COUNTRY_URL cache")
                        listener.success(mCountryMemoryCache.get(COUNTRY_URL))
                    }
                }

                override fun failed(message: String) {
                    listener.failed(message)

                }
            }
            downloadCountryList(cacheListener)

        } else if (cacheResult != null) {
            listener.success(cacheResult)
            Log.v(TAG, "Cache found, fetching from cache: ${mCountryMemoryCache.get(COUNTRY_URL)}")

        } else {
            mCountryMemoryCache.put(COUNTRY_URL, app.diskCache?.readCountryListFromDiskCache(COUNTRY_URL))
            listener.success(mCountryMemoryCache.get(COUNTRY_URL))
            Log.v(TAG, "DiskCache found fetching from diskCache: ${app.diskCache?.readCountryListFromDiskCache(COUNTRY_URL)}")
        }

    }


    fun downloadCountryList(listener: CountryLoadListener) {

        val builder = GsonBuilder()
        builder.registerTypeAdapter(Response::class.java, CountryListDeserializer())
        val gson = builder.create()

        val requestInterface = Retrofit.Builder()
                .baseUrl(COUNTRY_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(CurrencyConverterApi::class.java)

        call = requestInterface.getCountries()
        Log.d("REQUEST", call.toString() + "")

        call?.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>?) {
                if (response != null) {
                    val list = response.body()!!
                    Log.d("RESPONSE", "" + list.toString())

                    responseResult = ArrayList((list.results.countryContainer)
                            .sortedWith(compareBy { it.name }))
                    listener.success(ArrayList(responseResult));

                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Log.d(TAG, t.localizedMessage)
                listener.failed("Error");
            }
        })
    }


    override fun countryFetchCancel() {
        call?.cancel()
        Log.v(TAG, "Call is canceled: ${call?.isCanceled}")
    }

}