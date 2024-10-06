package com.example.e_commerce_app.favorite.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce_app.R
import com.example.e_commerce_app.model.product.Product

class FavoriteAdapter(
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var favoriteProducts: List<Product> = listOf()

    fun submitList(products: List<Product>) {
        favoriteProducts = products
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fav_product, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val product = favoriteProducts[position]
        holder.bind(product, onDeleteClick)
    }

    override fun getItemCount(): Int {
        return favoriteProducts.size
    }

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)

        fun bind(product: Product, onDeleteClick: (Product) -> Unit) {
            productName.text = product.title

            val price = product.variants.firstOrNull()?.price ?: "N/A"
            productPrice.text = "$$price"

            // Access the image URL safely
            val imageUrl = product.image?.src
            if (!imageUrl.isNullOrEmpty()) {
                Log.d(
                    "FavoriteAdapter",
                    "Loading image for product: ${product.title}, Image URL: $imageUrl"
                )

                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.nofav2)
                    .error(R.drawable.delete_svg)
                    .into(productImage)
            } else {
                productImage.setImageResource(R.drawable.nofav2)
                Log.d("FavoriteAdapter", "No valid image URL for product: ${product.title}")
            }

            deleteIcon.setOnClickListener {
                onDeleteClick(product)
            }
        }
    }
}
