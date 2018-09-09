package com.example.pavelsvetlugins.currencyexchange

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.recycler_view_rate.view.*


class CurrencyAdapter( private val currencyList: ArrayList<LocalCurrency>,
                       private val listener: Listener) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    interface Listener{
        fun onItemClick(localCurrency: LocalCurrency)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_rate, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = currencyList.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(currencyList[position], listener)
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){

        fun bind(localCurrency: LocalCurrency, listener: Listener){
             itemView.currency_name.text = localCurrency.currency
            itemView.currency_value.text = localCurrency.rate.toString()
            itemView.setOnClickListener { listener.onItemClick(localCurrency) }
        }

    }

}