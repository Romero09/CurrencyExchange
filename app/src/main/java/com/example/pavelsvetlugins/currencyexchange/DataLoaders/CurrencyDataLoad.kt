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


class CurrencyDataLoad(): CurrencyFetchData {

    var call: Call<Rates>? = null

    val cacheSize = (Runtime.getRuntime().maxMemory().toInt())/8

    val TAG = CurrencyDataLoad::class.java.simpleName

    override val mCurrencyMemoryCache =
            object : LruCache<String, Pair<Date, ArrayList<LocalCurrency>>>(cacheSize) {}

    override val CURRENCY_URL = "http://data.fixer.io"

    class CurrencyListDeserializer : JsonDeserializer<Rates> {

        @Throws(JsonParseException::class)
        override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Rates {
            Log.v("Compiles tag RATES", "it compiles")
            val jsonObject = element.asJsonObject.get("rates").asJsonObject
            Log.v("some object RATES", element.toString())
            Log.v("object RATES", jsonObject.toString())
            val countryList = ArrayList<LocalCurrency>()
            for ((key, value) in jsonObject.entrySet()) {
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

    override fun loadCurrencyList(listener: CurrencyLoadListener) {
        val builder = GsonBuilder()
        builder.registerTypeAdapter(Rates::class.java, CurrencyListDeserializer())
        val gson = builder.create()

        val requestInterface = Retrofit.Builder()
                .baseUrl(CURRENCY_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(CurrencyConverterApi::class.java)

        call = requestInterface.getCurrency()
        Log.d("REQUEST  RATES", call.toString() + "")

        call?.enqueue(object : Callback<Rates> {
            override fun onResponse(call: Call<Rates>, response: retrofit2.Response<Rates>?) {
                if (response != null) {
                    val list = response.body()!!
                    Log.d("RESPONSE", "" + list.toString())

                    val calendarNow = Calendar.getInstance()
                    calendarNow.add(Calendar.HOUR, 1)
                    calendarNow.set(Calendar.MINUTE, 0)
                    calendarNow.set(Calendar.SECOND, 0)

                    if(mCurrencyMemoryCache.get(CURRENCY_URL) == null){
                        mCurrencyMemoryCache.put(CURRENCY_URL, calendarNow.time to ArrayList((list.currency)))
                        Log.v(TAG, "Cache was null, creating new CURRENCY_URL cache")
                    }
                    Log.v(TAG, "Cache found: ${mCurrencyMemoryCache.get(CURRENCY_URL)}")


                    listener.success(ArrayList(list.currency))
                }
            }

            override fun onFailure(call: Call<Rates>, t: Throwable) {
                Log.d(TAG, t.localizedMessage)
                listener.failed("Error")
            }
        })
    }



    override fun currencyFetchCancel() {
        call?.cancel()
        Log.v(TAG, "Call is canceled: ${call?.isCanceled}")
    }

}