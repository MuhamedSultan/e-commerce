package com.example.e_commerce_app.product_details.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R

class SizeAdapter(private val sizes: List<String>) : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    class SizeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sizeTextView: TextView = itemView.findViewById(R.id.size_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_size, parent, false)
        return SizeViewHolder(view)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.sizeTextView.text = sizes[position]
    }

    override fun getItemCount() = sizes.size
}
