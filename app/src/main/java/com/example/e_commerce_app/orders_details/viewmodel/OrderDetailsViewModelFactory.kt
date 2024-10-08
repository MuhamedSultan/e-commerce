package com.example.e_commerce_app.orders_details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.model.repo.ShopifyRepo

class OrderDetailsViewModelFactory(
    private val shopifyRepo: ShopifyRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrderDetailsViewModel(shopifyRepo) as T
    }
}