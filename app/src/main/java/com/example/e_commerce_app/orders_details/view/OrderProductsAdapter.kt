package com.example.e_commerce_app.orders_details.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.ItemOrderDetailsProductBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.order_details.LineItem
import com.example.e_commerce_app.model.order_details.Order

class OrderProductsAdapter(
    private val itemsList: MutableList<LineItem>,
    private val onProductClick: (LineItem) -> Unit,
    private val context: Context,
    private val src: List<String>
) :
    RecyclerView.Adapter<OrderProductsAdapter.OrderProductsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderDetailsProductBinding.inflate(inflater, parent, false)
        return OrderProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderProductsViewHolder, position: Int) {
        val item = itemsList[position]
        Glide.with(context).load(src[position])
            .placeholder(R.drawable.box).error(R.drawable.box)
            .into(holder.image)
        holder.productName.text = item.title
        val defaultPrice = item.price.toDoubleOrNull() ?: 0.0
        holder.productPrice.text = LocalDataSourceImpl.getPriceAndCurrency(defaultPrice)

        holder.productItem.setOnClickListener {
            onProductClick(item)
        }
    }

    override fun getItemCount(): Int {

        return itemsList.size
    }

    fun removeFirstItem() {
        if (itemsList.isNotEmpty()) {
            itemsList.removeAt(0)
            notifyItemRemoved(0)
            notifyItemRangeChanged(0, itemsList.size)
        }
    }

    class OrderProductsViewHolder(binding: ItemOrderDetailsProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val productItem = binding.root
        val image = binding.productImage
        val productName = binding.productNameTv
        val productPrice = binding.productPriceTv
    }
}
