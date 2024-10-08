package com.example.e_commerce_app.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.model.cart.LineItem
import com.bumptech.glide.Glide

class CartAdapter(
    private var lineItems: List<LineItem>,  // Change from List<Cart> to List<LineItem>
    private val onDeleteClick: (LineItem) -> Unit,
    private val onIncreaseQuantity: (LineItem) -> Unit,
    private val onDecreaseQuantity: (LineItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.cart_product_image)
        private val productTitle: TextView = itemView.findViewById(R.id.cart_product_title)
        private val productPrice: TextView = itemView.findViewById(R.id.cart_product_price)
        private val amountTxt: TextView = itemView.findViewById(R.id.amountTxt)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_cart_item_Btn)
        private val plusButton: ImageButton = itemView.findViewById(R.id.plusBtn)
        private val minusButton: ImageButton = itemView.findViewById(R.id.minusBtn)

        fun bind(lineItem: LineItem) {
            productTitle.text = lineItem.title
            productPrice.text = lineItem.price
            amountTxt.text = lineItem.quantity.toString()

            // Load image using Glide
            /*Glide.with(itemView.context)
                .load(lineItem.imageUrl)
                .into(productImage)*/
            Glide.with(itemView.context)
                .load(lineItem.imageUrl)
                .placeholder(R.drawable.discount_place_holder) // Optional placeholder
                .error(R.drawable.discount_place_holder) // Optional error image
                .into(productImage)

            deleteButton.setOnClickListener {
                onDeleteClick(lineItem)
            }

            plusButton.setOnClickListener {
                onIncreaseQuantity(lineItem)
            }

            minusButton.setOnClickListener {
                onDecreaseQuantity(lineItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val lineItem = lineItems[position]  // Access the LineItem directly
        holder.bind(lineItem)
    }

    override fun getItemCount(): Int {
        return lineItems.size  // Return the size of lineItems
    }

    // Method to update data in the adapter
    fun updateData(newLineItems: List<LineItem>) {
        lineItems = newLineItems
        notifyDataSetChanged() // Notify the adapter that data has changed
    }

}
