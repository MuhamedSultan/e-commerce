package com.example.e_commerce_app.categories.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.ItemChipBinding
import com.example.e_commerce_app.model.custom_collection.CustomCollection

class CategoriesAdapter(
    private val collectionsList: List<CustomCollection>,
    private val onCategoryClick: OnCategoryClick
) : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val view = ItemChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val collection = collectionsList[position]
        holder.categoryItem.text = collection.title

        holder.categoryItem.setOnClickListener {
            onCategoryClick.onCategoryClick(collection.id)
            holder.categoryItem.setBackgroundResource(R.color.yellow)
        }
    }

    override fun getItemCount(): Int {
        return collectionsList.size
    }

    class CategoriesViewHolder(binding: ItemChipBinding) : RecyclerView.ViewHolder(binding.root) {
        val categoryItem = binding.chip
    }
}
