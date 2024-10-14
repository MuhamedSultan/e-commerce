package com.example.e_commerce_app.orders.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.databinding.ItemOrdersBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.orders.Order

class OrdersAdapter(
    private val ordersList: List<Order>,
    private val onOrderClick: (Order) -> Unit,
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
        holder.orderNumber.text = order.order_number.toString()
        holder.quantity.text = (order.line_items.size - 1).toString()
        val createdAt = order.created_at
        holder.createAt.text = createdAt.substring(0,10)
        val defaultPrice = order.total_price.toDoubleOrNull() ?: 0.0
        val convertedPrice = LocalDataSourceImpl.getPriceAndCurrency(defaultPrice)
        holder.orderPrice.text = convertedPrice
        holder.orderItem.setOnClickListener {
            onOrderClick(order)
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
        val quantity = binding.quantityTv
        val createAt = binding.createAt
    }
}
