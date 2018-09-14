package com.example.pavelsvetlugins.currencyexchange.DataLoaders

import android.content.Context
import android.os.Environment
import android.os.Environment.isExternalStorageRemovable
import android.util.Log
import com.example.pavelsvetlugins.currencyexchange.CountryDetails
import com.example.pavelsvetlugins.currencyexchange.LocalCurrency
import com.jakewharton.disklrucache.DiskLruCache
import java.io.*
import java.util.*
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

class DiskCache(val context: Context) {


    val TAG = DiskCache::class.java.simpleName
    private val DISK_CACHE_SIZE = 1024 * 1024 * 10 // 10MB
    private val DISK_CACHE_SUBDIR = "thumbnails"
    private var mDiskLruCache: DiskLruCache? = null
    private val mDiskCacheLock = ReentrantLock()
    private val mDiskCacheLockCondition: Condition = mDiskCacheLock.newCondition()
    private var mDiskCacheStarting = true

    val cacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR)

    init {
        InitDiskCacheTask().start(cacheDir)
    }


    internal inner class InitDiskCacheTask {
        fun start(vararg params: File): Void? {
            Log.v(TAG, "create LRU cache in:" + cacheDir.getAbsolutePath());
            val cacheDir = params[0]
            mDiskLruCache = DiskLruCache.open(cacheDir, DISK_CACHE_SIZE, 1, 10 * 1024 * 1024)
            mDiskCacheStarting = false // Finished initialization
            return null
        }
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
// but if not mounted, falls back on internal storage.
    fun getDiskCacheDir(context: Context, uniqueName: String): File {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        val cachePath =
                if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                        || !isExternalStorageRemovable()) {
                    context.externalCacheDir.path
                } else {
                    context.cacheDir.path
                }
        return File(cachePath + File.separator + uniqueName)
    }


    fun writeCountryListToDiskCache(key: String, countryDetailsList: ArrayList<CountryDetails>): Boolean {
        var isOk = false
        if (mDiskLruCache != null) {
            val cacheKey = key.hashCode().toString()
            try {

                var editor = mDiskLruCache?.edit(cacheKey)
                if (editor != null) {
                    val out = editor!!.newOutputStream(0)
                    val oos = ObjectOutputStream(out)
                    oos.writeObject(countryDetailsList);
                    oos.close()
                    out.close()
                    editor!!.commit()
                    Log.d(TAG, "write to disk key:$cacheKey, url:$cacheKey");
                    isOk = true
                    editor = null
                }
            } catch (e: IOException) {
                isOk = false
                Log.v(TAG, "write disk lru cache error:" + e.toString(), e)
            }

        } else {
            Log.v(TAG, "read disk lru cache is null")
        }
        return isOk
    }


    fun readCountryListFromDiskCache(key: String): ArrayList<CountryDetails>? {
        var country: ArrayList<CountryDetails>? = null
        if (mDiskLruCache != null) {
            //diskCache.flush()
            val cacheKey = key.hashCode().toString()
            try {
                var snapshot = mDiskLruCache?.get(cacheKey)
                if (snapshot != null) {
                    val input: InputStream = snapshot!!.getInputStream(0)
                    val inputObject: ObjectInputStream = ObjectInputStream(input)
                    country = inputObject.readObject() as ArrayList<CountryDetails>
                    inputObject.close()
                    input.close()
                    snapshot!!.close()
                    snapshot = null
                    Log.d(TAG, "read from disk key:$cacheKey, url:$cacheKey");
                } else {
                    Log.v(TAG, "read disk lru cache error: snapshot is null")
                }
            } catch (e: IOException) {
                Log.v(TAG, "read disk lru cache error:" + e.toString(), e)
            }

        } else {
            Log.v(TAG, "read disk lru cache is null")
        }
        return country
    }



    fun writeCurrencyListToDiskCache(key: String, localCurrencyList: Pair<Date, ArrayList<LocalCurrency>>): Boolean {
        var isOk = false
        if (mDiskLruCache != null) {
            val cacheKey = key.hashCode().toString()
            try {

                var editor = mDiskLruCache?.edit(cacheKey)
                if (editor != null) {
                    val out = editor!!.newOutputStream(0)
                    val oos = ObjectOutputStream(out)
                    oos.writeObject(localCurrencyList);
                    oos.close()
                    out.close()
                    editor!!.commit()
                    Log.d(TAG, "write to disk key:$cacheKey, url:$cacheKey");
                    isOk = true
                    editor = null
                }
            } catch (e: IOException) {
                isOk = false
                Log.v(TAG, "write disk lru cache error:" + e.toString(), e)
            }

        } else {
            Log.v(TAG, "read disk lru cache is null")
        }
        return isOk
    }


    fun readCurrencyListFromDiskCache(key: String): Pair<Date, ArrayList<LocalCurrency>>? {
        var country: Pair<Date, ArrayList<LocalCurrency>>? = null
        if (mDiskLruCache != null) {
            //diskCache.flush()
            val cacheKey = key.hashCode().toString()
            try {
                var snapshot = mDiskLruCache?.get(cacheKey)
                if (snapshot != null) {
                    val input: InputStream = snapshot!!.getInputStream(0)
                    val inputObject: ObjectInputStream = ObjectInputStream(input)
                    country = inputObject.readObject() as Pair<Date, ArrayList<LocalCurrency>>
                    inputObject.close()
                    input.close()
                    snapshot!!.close()
                    snapshot = null
                    Log.d(TAG, "read from disk key:$cacheKey, url:$cacheKey");
                } else {
                    Log.v(TAG, "read disk lru cache error: snapshot is null")
                }
            } catch (e: IOException) {
                Log.v(TAG, "read disk lru cache error:" + e.toString(), e)
            }

        } else {
            Log.v(TAG, "read disk lru cache is null")
        }
        return country
    }


}