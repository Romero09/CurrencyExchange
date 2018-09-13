package com.example.pavelsvetlugins.currencyexchange.DataLoaders

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
import java.util.*


class CountryDataLoad() : CountryFetchData {

    val TAG = CountryDataLoad::class.java.simpleName

    var call: Call<Response>? = null

    val cacheSize = (Runtime.getRuntime().maxMemory().toInt()) / 8

    override val mCountryMemoryCache =
            object : LruCache<String, ArrayList<CurrencyDetails>>(cacheSize) {}

    override val COUNTRY_URL = "https://free.currencyconverterapi.com"

    class CountryListDeserializer : JsonDeserializer<Response> {

        @Throws(JsonParseException::class)
        override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Response {
            Log.v("Compiles tag", "it compiles")
            val jsonObject = element.asJsonObject.get("results").asJsonObject
            Log.v("some object", element.toString())
            Log.v("object", jsonObject.toString())
            val countryList = ArrayList<CurrencyDetails>()
            for ((_, value) in jsonObject.entrySet()) {
                // For individual Country objects, we can use default deserialisation:
                val city = context.deserialize<CurrencyDetails>(value, CurrencyDetails::class.java)
                countryList.add(city)
            }
            Log.v("Country List", countryList.toString())
            return Response(ResponseCountryList(countryList))
        }
    }


    override fun loadCountryList(listener: CountryLoadListener) {
        Log.v(TAG, "Cache size $cacheSize")


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

                    if (mCountryMemoryCache.get(COUNTRY_URL) == null ) {
                        mCountryMemoryCache.put(COUNTRY_URL, ArrayList((list.results.currencyContainer)
                                .sortedWith(compareBy { it.name })))
                        Log.v(TAG, "Cache was null, creating new COUNTRY_URL cache")
                    }

                    Log.v(TAG, "Cache found: ${mCountryMemoryCache.get(COUNTRY_URL)}")

                    listener.success(ArrayList((list.results.currencyContainer).sortedWith(compareBy { it.name })));
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