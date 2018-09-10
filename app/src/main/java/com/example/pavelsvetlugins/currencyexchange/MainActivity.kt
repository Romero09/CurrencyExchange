@file:Suppress("DEPRECATION")

package com.example.pavelsvetlugins.currencyexchange

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.example.pavelsvetlugins.currencyexchange.Fragments.CountryFragment
import com.example.pavelsvetlugins.currencyexchange.Fragments.CurrencyFragment
import com.example.pavelsvetlugins.currencyexchange.Fragments.FragmentAdapter
import kotlinx.android.synthetic.main.view_pager.*




class MainActivity : AppCompatActivity() {


    private val TAG = MainActivity::class.java.simpleName
    val mFragmentAdapter = FragmentAdapter(supportFragmentManager)
    lateinit var mViewPager: ViewPager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        mViewPager = container

        setUpViewPager(mViewPager)

        mViewPager.setCurrentItem(0)

    }

    override fun onBackPressed() {
        if(mViewPager.currentItem == 1){
        mViewPager.setCurrentItem(0)}
        else{
        this.finish()
            }
    }

    fun setUpViewPager(viewPager: ViewPager){
        val fragmentAdapter = FragmentAdapter(supportFragmentManager)
        fragmentAdapter.addFragment(CountryFragment())
        fragmentAdapter.addFragment(CurrencyFragment())
        viewPager.setAdapter(fragmentAdapter)

    }

    fun setViewPager(fragmentNumber: Int){
        mViewPager.setCurrentItem(fragmentNumber)
    }


















}