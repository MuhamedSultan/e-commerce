package com.example.e_commerce_app.brand_products.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.home.viewmodel.HomeViewModel
import com.example.e_commerce_app.model.repo.ShopifyRepo

class BrandProductViewModelFactory(private val shopifyRepo: ShopifyRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BrandProductViewModel(shopifyRepo) as T
    }

}