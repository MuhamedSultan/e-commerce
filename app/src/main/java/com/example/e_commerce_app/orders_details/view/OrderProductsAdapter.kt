package com.example.e_commerce_app.orders_details.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.ItemOrderDetailsProductBinding
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.order_details.LineItem
import com.example.e_commerce_app.model.order_details.Order

class OrderProductsAdapter(
    private val itemsList: MutableList<LineItem>,
    private val onProductClick: (LineItem) -> Unit,
    private val currencyResponse: CurrencyResponse,
    private val selectedCurrency: String,
    private var conversionRate:Double
) :
    RecyclerView.Adapter<OrderProductsAdapter.OrderProductsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderDetailsProductBinding.inflate(inflater, parent, false)
        return OrderProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderProductsViewHolder, position: Int) {
        val item = itemsList[position]
        holder.image.setImageResource(R.drawable.box)
        holder.productName.text = item.title
        val defaultPrice = item.price.toDoubleOrNull()?:0.0
        val convertedPrice = defaultPrice * conversionRate
        holder.productPrice.text = String.format("%.2f %s", convertedPrice, selectedCurrency)

        holder.productItem.setOnClickListener {
            onProductClick(item)
        }
    }

    override fun getItemCount(): Int {

        return itemsList.size
    }
    fun removeFirstItem() {
        if (itemsList.isNotEmpty()) {
            itemsList.removeAt(0)
            notifyItemRemoved(0)
            notifyItemRangeChanged(0, itemsList.size)
        }

        conversionRate = when (selectedCurrency) {
            "USD" -> currencyResponse.rates.USD
            "EUR" -> currencyResponse.rates.EUR
            "EGP" -> currencyResponse.rates.EGP
            else ->0.0
        }
    }
    class OrderProductsViewHolder(binding: ItemOrderDetailsProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val productItem = binding.root
        val image = binding.productImage
        val productName = binding.productNameTv
        val productPrice = binding.productPriceTv
    }
}
