package com.example.pavelsvetlugins.currencyexchange

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.recycler_view_country.view.*

class CountryAdapter(private val countryList: ArrayList<CurrencyDetails>,
                     private val listener: (CurrencyDetails) -> Unit) : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(countryList[position], listener)
    }

    override fun getItemCount(): Int = countryList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_country, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(currencyDetails: CurrencyDetails, listener: (CurrencyDetails) -> Unit)  = with(itemView) {

            itemView.country_name.text = currencyDetails.name
            setOnClickListener { listener(currencyDetails) }
        }
    }
}