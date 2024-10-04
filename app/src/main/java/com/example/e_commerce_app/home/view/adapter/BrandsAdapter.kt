package com.example.e_commerce_app.home.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.e_commerce_app.databinding.ItemBrandBinding
import com.example.e_commerce_app.model.smart_collection.SmartCollection

class BrandsAdapter(private val smartCollection: List<SmartCollection>, private val context: Context) :
    Adapter<BrandsAdapter.SmartCollectionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmartCollectionViewHolder {
       val view = ItemBrandBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SmartCollectionViewHolder(view)
    }

    override fun getItemCount(): Int {
      return smartCollection.size
    }

    override fun onBindViewHolder(holder: SmartCollectionViewHolder, position: Int) {
       val collection=smartCollection[position]
        Glide.with(context).load(collection.image.src).into(holder.brandImage)
    }

    class SmartCollectionViewHolder(private val binding: ItemBrandBinding) :
        ViewHolder(binding.root) {
        val brandImage = binding.brandImage
    }

}