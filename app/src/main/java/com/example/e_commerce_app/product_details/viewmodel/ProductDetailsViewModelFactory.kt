package com.example.e_commerce_app.product_details.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.model.repo.ShopifyRepo

class ProductDetailsViewModelFactory(
    private val shopifyRepo: ShopifyRepo,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)) {
            return ProductDetailsViewModel(shopifyRepo, ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
