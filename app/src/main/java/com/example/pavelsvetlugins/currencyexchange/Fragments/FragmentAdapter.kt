package com.example.pavelsvetlugins.currencyexchange.Fragments

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

open class FragmentAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

    open var fragmentList: MutableList<Fragment> = mutableListOf()

    open fun addFragment(fragment: Fragment) = fragmentList.add(fragment)

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

}