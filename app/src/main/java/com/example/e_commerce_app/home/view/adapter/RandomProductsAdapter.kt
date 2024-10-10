package com.example.e_commerce_app.home.view.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.ItemProductBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.product.Product

class RandomProductsAdapter(
    private val productList: List<Product>,
    private val context: Context,
    private val onProductClick: (Product) -> Unit,
    private val onFavouriteClick: (Product, Boolean) -> Unit,
    private val sharedPreferences: SharedPreferences,
    private val currencyResponse: CurrencyResponse,
    private val selectedCurrency: String,
    private var conversionRate:Double


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
        val defaultPrice = product.variants[0].price.toDoubleOrNull() ?: 0.0

        val convertedPrice = defaultPrice * conversionRate
        holder.productPrice.text = String.format("%.2f %s", convertedPrice, selectedCurrency)

        var isFavorite = LocalDataSourceImpl.isProductFavorite(context, product.id.toString())
        holder.favouriteIcon.setImageResource(
            if (isFavorite) R.drawable.ic_favourite_fill
            else R.drawable.ic_favourite_border
        )
        holder.itemView.setOnClickListener { onProductClick(product) }

        holder.favouriteIcon.setOnClickListener {
            val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
            if (shopifyCustomerId != null) {
                isFavorite = !isFavorite
                onFavouriteClick(product, isFavorite)
                holder.favouriteIcon.setImageResource(
                    if (isFavorite) R.drawable.ic_favourite_fill
                    else R.drawable.ic_favourite_border
                )
            } else {
                Toast.makeText(
                    it.context,
                    "Please log in to add favorites",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

         conversionRate = when (selectedCurrency) {
            "USD" -> currencyResponse.rates.USD
            "EUR" -> currencyResponse.rates.EUR
            "EGP" -> currencyResponse.rates.EGP
             else ->0.0
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