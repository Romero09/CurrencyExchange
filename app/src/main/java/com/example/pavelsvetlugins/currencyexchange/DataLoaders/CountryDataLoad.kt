package com.example.pavelsvetlugins.currencyexchange.DataLoaders

import android.util.Log
import com.example.pavelsvetlugins.currencyexchange.*
import com.google.gson.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*

class CountryDataLoad(){

    var status = "none"

    private var mCurrencyDetailsList: ArrayList<CurrencyDetails> = ArrayList<CurrencyDetails>()

    val TAG = CountryDataLoad::class.java.simpleName

    private val BASE_URL = "https://free.currencyconverterapi.com"

    val model = SharedViewModel()

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


    fun loadCountryList() : String {

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

                    val list = response.body()!!
                    Log.d("RESPONSE", "" + list.toString())
                    model.countryList = ArrayList((list.results.currencyContainer).sortedWith(compareBy{ it.name }))

                    status = "Ok"
                    Log.v(TAG, status)
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Log.d(TAG, t.localizedMessage)
                status = "Error"
                Log.v(TAG, status)
            }
        })
        return "nothing"
    }


}