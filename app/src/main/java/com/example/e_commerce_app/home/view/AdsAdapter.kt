package com.example.e_commerce_app.home.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.ItemBrandBinding
import com.example.e_commerce_app.databinding.ItemCouponBinding
import com.example.e_commerce_app.model.coupon.Discount

class DiscountPagerAdapter(
    private val discountList: List<Discount>
) : RecyclerView.Adapter<DiscountPagerAdapter.DiscountViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coupon, parent, false)
        return DiscountViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiscountViewHolder, position: Int) {
        val discount = discountList[position]
        holder.bind(discount)
    }

    override fun getItemCount(): Int = discountList.size

    inner class DiscountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.discountImageView)

        fun bind(discount: Discount) {
            // Set discount image
//            imageView.setImageResource(discount.imageResId)
            Glide.with(itemView.context)
                .load(discount.imageUrl)
                .placeholder(R.drawable.ic_launcher_background) // Optional placeholder
                .error(R.drawable.ic_launcher_foreground) // Optional error image
                .into(imageView)
            // Set long-click listener to copy discount code
            imageView.setOnLongClickListener {
                // Copy discount code to clipboard
                val clipboard = itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Discount Code", discount.code)
                clipboard.setPrimaryClip(clip)

                // Show confirmation (optional)
                Toast.makeText(itemView.context, "Discount code copied!", Toast.LENGTH_SHORT).show()

                true
            }
        }
    }
}
