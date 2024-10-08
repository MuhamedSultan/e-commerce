package com.example.e_commerce_app.orders.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.databinding.ItemOrdersBinding
import com.example.e_commerce_app.model.orders.Order

class OrdersAdapter(private val ordersList: List<Order>, private val onOrderClick: (Order) -> Unit) :
    RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    private var isExpanded = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = ItemOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = ordersList[position]
        holder.orderNumber.text = ("#${position + 1}").toString()
        holder.orderPrice.text = order.total_price
        holder.createdAt.text = order.created_at
        holder.currency.text = order.currency
        holder.orderItem.setOnClickListener {
            onOrderClick(order)
        }
    }

    override fun getItemCount(): Int {
        return if (isExpanded) ordersList.size else 2
    }

    fun viewMore() {
        isExpanded = !isExpanded
        notifyDataSetChanged()
    }

    class OrdersViewHolder(binding: ItemOrdersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val orderItem = binding.orderItem
        val orderNumber = binding.orderNumberTv
        val orderPrice = binding.OrderPriceTv
        val createdAt = binding.createdAtTv
        val currency = binding.currencyTv
    }
}
