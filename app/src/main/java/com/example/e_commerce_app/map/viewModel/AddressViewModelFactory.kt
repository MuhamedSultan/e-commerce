package com.example.e_commerce_app.map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.brand_products.viewmodel.BrandProductViewModel
import com.example.e_commerce_app.model.repo.ShopifyRepo

class AddressViewModelFactory (private val _repo :ShopifyRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddressViewModel(_repo) as T
    }

}