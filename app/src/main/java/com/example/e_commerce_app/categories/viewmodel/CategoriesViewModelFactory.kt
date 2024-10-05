package com.example.e_commerce_app.categories.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.home.viewmodel.HomeViewModel
import com.example.e_commerce_app.model.repo.ShopifyRepo

class CategoriesViewModelFactory( private val shopifyRepo: ShopifyRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoriesViewModel(shopifyRepo) as T
    }
}
