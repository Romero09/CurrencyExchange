package com.example.pavelsvetlugins.currencyexchange.DataLoaders

import android.content.Context
import android.util.Log
import android.util.LruCache
import com.example.pavelsvetlugins.currencyexchange.CurrencyConverterApi
import com.example.pavelsvetlugins.currencyexchange.LocalCurrency
import com.example.pavelsvetlugins.currencyexchange.MyApplication
import com.example.pavelsvetlugins.currencyexchange.Rates
import com.google.gson.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*


class CurrencyDataLoad(context: Context): CurrencyFetchData {

    var call: Call<Rates>? = null

    val cacheSize = (Runtime.getRuntime().maxMemory().toInt())/8

    val TAG = CurrencyDataLoad::class.java.simpleName

    override val mCurrencyMemoryCache = LruCache<String, Pair<Date, ArrayList<LocalCurrency>>>(cacheSize)

    var app = context.getApplicationContext() as MyApplication

    override val CURRENCY_URL = "http://data.fixer.io"

    var responseResult: Pair<Date, ArrayList<LocalCurrency>>? = null

    class CurrencyListDeserializer : JsonDeserializer<Rates> {

        @Throws(JsonParseException::class)
        override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Rates {
            val jsonObject = element.asJsonObject.get("rates").asJsonObject
            Log.v("some object RATES", element.toString())
            Log.v("object RATES", jsonObject.toString())
            val countryList = ArrayList<LocalCurrency>()
            for ((key, value) in jsonObject.entrySet()) {
                // For individual City objects, we can use default deserialisation:
                val currency = key
                val country = context.deserialize<Double>(value, Double::class.java)
                //Log.v("KEY RATES", key)
                //Log.v("deserialize RATES", country.toString())
                val rate = LocalCurrency(key, context.deserialize(value, Double::class.java))
                countryList.add(rate)
            }
            Log.v("Country List", countryList.toString())
            return Rates(countryList)
        }
    }



    override fun loadCurrencyList(listener: CurrencyLoadListener) {
        Log.v(TAG, "Cache size $cacheSize")

        val diskCacheResult = app.diskCache?.readCurrencyListFromDiskCache(CURRENCY_URL)
        val cacheResult = mCurrencyMemoryCache.get(CURRENCY_URL)

        if (diskCacheResult == null || isUpToDate(diskCacheResult)) {
            Log.v(TAG, "Cache state diskCacheResult- $diskCacheResult")
            Log.v(TAG, "Cache state cacheResult- $cacheResult")
            val cacheListener = object : CurrencyLoadListener{
                override fun success(response: Pair<Date, ArrayList<LocalCurrency>>) {

                    app.diskCache?.writeCurrencyListToDiskCache(CURRENCY_URL, responseResult!!)
                    Log.v(TAG, "DiskCache was null or old, creating new COUNTRY_URL cache")

                    if (cacheResult == null || isUpToDate(cacheResult)) {
                        mCurrencyMemoryCache.put(CURRENCY_URL, responseResult)
                        Log.v(TAG, "Cache was null ord old, creating new COUNTRY_URL cache")
                        listener.success(mCurrencyMemoryCache.get(CURRENCY_URL))
                    }
                }

                override fun failed(message: String) {
                    listener.failed(message)

                }
            }

            downloadCurrencyList(cacheListener)

        } else if (cacheResult != null) {
            listener.success(cacheResult)
            Log.v(TAG, "Cache found, fetching from cache")

        } else {
            mCurrencyMemoryCache.put(CURRENCY_URL, diskCacheResult)
            listener.success(mCurrencyMemoryCache.get(CURRENCY_URL))
            Log.v(TAG, "DiskCache found fetching from diskCache")
        }

    }





    fun downloadCurrencyList(listener: CurrencyLoadListener) {
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

                    responseResult = calendarNow.time to ArrayList(list.currency)


                    listener.success(responseResult!!)
                }
            }

            override fun onFailure(call: Call<Rates>, t: Throwable) {
                Log.d(TAG, t.localizedMessage)
                listener.failed("Error")
            }
        })
    }


    private fun isUpToDate(currencyListPair: Pair<Date, java.util.ArrayList<LocalCurrency>>?): Boolean {
        if (currencyListPair != null) {
            val storedDate = currencyListPair.first
            val nowDate = Calendar.getInstance().time
            Log.v(TAG, "Stored date $storedDate now date $nowDate it is: ${(nowDate < storedDate)}")
            if (nowDate > storedDate) {
                return true
            }
        }
        return false

    }



    override fun currencyFetchCancel() {
        call?.cancel()
        Log.v(TAG, "Call is canceled: ${call?.isCanceled}")
    }

}