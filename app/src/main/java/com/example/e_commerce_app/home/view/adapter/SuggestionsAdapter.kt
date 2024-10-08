package com.example.e_commerce_app.home.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.model.product.Product

class SuggestionsAdapter(
    private var suggestions: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<SuggestionsAdapter.SuggestionViewHolder>() {


    fun updateProducts(newProducts: List<Product>) {
        suggestions = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.bind(suggestion, onClick)
    }

    override fun getItemCount(): Int = suggestions.size

    class SuggestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.suggestionText)

        fun bind(suggestion: Product, onClick: (Product) -> Unit) {
            textView.text = suggestion.title
            itemView.setOnClickListener { onClick(suggestion) }
        }
    }
}