package com.example.e_commerce_app.product_details.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.model.rateing.RatingItem

class RatingAdapter(private val ratingList: List<RatingItem>) :
    RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {

    inner class RatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personImage: ImageView = itemView.findViewById(R.id.person_image)
        val personName: TextView = itemView.findViewById(R.id.person_name)
        val ratingDescription: TextView = itemView.findViewById(R.id.rating_description)
        val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating, parent, false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val ratingItem = ratingList[position]
        holder.personImage.setImageResource(ratingItem.personImage)
        holder.personName.text = ratingItem.personName
        holder.ratingDescription.text = ratingItem.ratingDescription
        holder.ratingBar.rating = ratingItem.rating

    }

    override fun getItemCount(): Int {
        return ratingList.size
    }
}
