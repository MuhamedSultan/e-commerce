package com.example.e_commerce_app.categories.view.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.ItemBrandProductBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.model.product.Product

class CategoriesProductsAdapter(
    private val productList: List<Product>,
    private val context: Context,
    private val onProductClick: (Product) -> (Unit),
    private val onFavouriteClick: (Product, Boolean) -> Unit,
    private val sharedPrefs: SharedPreferences

) :
    Adapter<CategoriesProductsAdapter.CategoriesProductsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriesProductsViewHolder {
        val view =
            ItemBrandProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriesProductsViewHolder, position: Int) {
        val product = productList[position]
        Glide.with(context).load(product.image.src).into(holder.productImage)
        holder.productName.text = product.title.split('|').getOrNull(1)?.trim() ?: ""
        holder.productPrice.text = product.variants[0].price
        var isFavorite = LocalDataSourceImpl.isProductFavorite(context, product.id.toString())
        holder.favouriteIcon.setImageResource(
            if (isFavorite) R.drawable.ic_favourite_fill
            else R.drawable.ic_favourite_border
        )
        holder.itemView.setOnClickListener { onProductClick(product) }

        holder.favouriteIcon.setOnClickListener {
            val shopifyCustomerId = sharedPrefs.getString("shopifyCustomerId", null)
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
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class CategoriesProductsViewHolder(binding: ItemBrandProductBinding) :
        ViewHolder(binding.root) {
        val productImage = binding.productImage
        val productName = binding.productNameTv
        val productPrice = binding.productPriceTv
        val favouriteIcon = binding.favIcon
    }
}