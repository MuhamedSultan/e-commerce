package com.example.e_commerce_app.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.model.repo.ShopifyRepo

class CartViewModelFactory(private val repo: ShopifyRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
