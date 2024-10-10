package com.example.e_commerce_app.setting

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.ItemCurrencyBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl

class CurrencyAdapter(
    private val currencyList: List<String>,
    private val onCurrencyClick: (String) -> Unit,
    private val selectedPosition: Int,
    private val context :Context
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private var currentSelectedPosition: Int = selectedPosition

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency = currencyList[position]
        holder.currencyItem.text = currency

        if (position == currentSelectedPosition) {
            holder.currencyItem.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.basic_color)
            )
            holder.currencyItem.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.white)
            )
        } else {
            holder.currencyItem.setBackgroundColor(
                ContextCompat.getColor(context, R.color.white)
            )
            holder.currencyItem.setTextColor(
                ContextCompat.getColor(context, R.color.black)
            )
        }

        holder.currencyItem.setOnClickListener {
            val currencyText = holder.currencyItem.text.toString()
            val previousPosition = currentSelectedPosition
            currentSelectedPosition = position
            LocalDataSourceImpl.saveCurrencyText(context, currencyText)
            LocalDataSourceImpl.saveCurrencyColorState(context, currentSelectedPosition)
            onCurrencyClick(currencyText)
            notifyItemChanged(previousPosition)
            notifyItemChanged(currentSelectedPosition)
        }
    }

    override fun getItemCount(): Int = currencyList.size

    class CurrencyViewHolder(binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root) {
        val currencyItem = binding.btnCurrency
    }

}
