package com.example.e_commerce_app.orders.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.databinding.ItemOrdersBinding
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.orders.Order

class OrdersAdapter(
    private val ordersList: List<Order>,
    private val onOrderClick: (Order) -> Unit,
    private val currencyResponse: CurrencyResponse,
    private val selectedCurrency: String,
    private var conversionRate:Double
) :
    RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    private var isExpanded = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = ItemOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
//        if (ordersList.isEmpty()) {
//            return
//        }
        val order = ordersList[position]
        holder.orderNumber.text = ("#${position + 1}").toString()
        holder.createdAt.text = order.created_at
        val defaultPrice = order.total_price.toDoubleOrNull()?:0.0
        val convertedPrice = defaultPrice * conversionRate
        holder.orderPrice.text = String.format("%.2f %s", convertedPrice, selectedCurrency)
        holder.orderItem.setOnClickListener {
            onOrderClick(order)
        }
        conversionRate = when (selectedCurrency) {
            "USD" -> currencyResponse.rates.USD
            "EUR" -> currencyResponse.rates.EUR
            "EGP" -> currencyResponse.rates.EGP
            else ->0.0
        }

    }

    override fun getItemCount(): Int {
   //     if (!isExpanded && ordersList.isNotEmpty()&&ordersList.size==2) {
//            return 2
//        }
            return ordersList.size
    }

//    fun viewMore() {
//        isExpanded = !isExpanded
//        notifyDataSetChanged()
//    }

    class OrdersViewHolder(binding: ItemOrdersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val orderItem = binding.orderItem
        val orderNumber = binding.orderNumberTv
        val orderPrice = binding.OrderPriceTv
        val createdAt = binding.createdAtTv
    }
}
