package com.example.e_commerce_app.orders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.home.viewmodel.HomeViewModel
import com.example.e_commerce_app.model.repo.ShopifyRepo

class OrdersViewModelFactory(  private val shopifyRepo: ShopifyRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrdersViewModel(shopifyRepo) as T
    }
}