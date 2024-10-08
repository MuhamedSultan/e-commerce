package com.example.e_commerce_app.home.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.ItemProductBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.model.product.Product

class RandomProductsAdapter(
    private val productList: List<Product>,
    private val context: Context,
    private val onProductClick: (Product) -> Unit,
    private val onFavouriteClick: (Product, Boolean) -> Unit
) :
    Adapter<RandomProductsAdapter.RandomProductsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandomProductsViewHolder {
        val view = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RandomProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: RandomProductsViewHolder, position: Int) {
        val product = productList[position]
        Glide.with(context).load(product.image.src).into(holder.productImage)
        holder.productName.text = product.title.split('|').getOrNull(1)?.trim()
        holder.productPrice.text = product.variants[0].price
        var isFavorite = LocalDataSourceImpl.isMealFavorite(context, product.id.toString())
        holder.favouriteIcon.setImageResource(
            if (isFavorite) R.drawable.favfill
            else R.drawable.favadd
        )

        holder.itemView.setOnClickListener { onProductClick(product) }

        holder.favouriteIcon.setOnClickListener {
            isFavorite = !isFavorite
            onFavouriteClick(product, isFavorite)
            holder.favouriteIcon.setImageResource(
                if (isFavorite) R.drawable.favfill
                else R.drawable.favadd
            )
        }
    }

    class RandomProductsViewHolder(binding: ItemProductBinding) :
        ViewHolder(binding.root) {
        val productImage = binding.productImage
        val productName = binding.productNameTv
        val productPrice = binding.productPriceTv
        val favouriteIcon = binding.favouriteIcon
    }

}