package com.example.e_commerce_app.home.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.e_commerce_app.databinding.ItemProductBinding
import com.example.e_commerce_app.model.product.Product

class RandomProductsAdapter(
    private val product: List<Product>,
    private val context: Context,
    private val onProductClick: (Product) -> Unit
) :
    Adapter<RandomProductsAdapter.RandomProductsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandomProductsViewHolder {
        val view = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RandomProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return product.size
    }

    override fun onBindViewHolder(holder: RandomProductsViewHolder, position: Int) {
        val productList = product[position]
        Glide.with(context).load(productList.image.src).into(holder.productImage)
        holder.productName.text = productList.title.split('|').getOrNull(1)?.trim() ?: ""
        holder.productPrice.text = productList.variants[0].price


        holder.itemView.setOnClickListener {
            onProductClick(productList)
        }

    }

    class RandomProductsViewHolder(binding: ItemProductBinding) :
        ViewHolder(binding.root) {
        val productImage = binding.productImage
        val productName = binding.productNameTv
        val productPrice = binding.productPriceTv
    }

}