package com.example.e_commerce_app.orders_details.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.ItemOrderDetailsProductBinding
import com.example.e_commerce_app.model.order_details.LineItem

class OrderProductsAdapter(
    private val itemsList: List<LineItem>,
    private val onProductClick: (LineItem) -> Unit
) :
    RecyclerView.Adapter<OrderProductsAdapter.OrderProductsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderDetailsProductBinding.inflate(inflater, parent, false)
        return OrderProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderProductsViewHolder, position: Int) {
        val item = itemsList[position]
        holder.image.setImageResource(R.drawable.box)
        holder.productName.text = item.title
        holder.productPrice.text = "${item.price}  EGP"
        holder.productItem.setOnClickListener {
            onProductClick(item)
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    class OrderProductsViewHolder(binding: ItemOrderDetailsProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val productItem = binding.root
        val image = binding.productImage
        val productName = binding.productNameTv
        val productPrice = binding.productPriceTv
    }
}
