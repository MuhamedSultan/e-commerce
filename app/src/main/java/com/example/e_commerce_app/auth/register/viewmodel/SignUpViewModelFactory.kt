package com.example.e_commerce_app.auth.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.model.repo.ShopifyRepo


class SignUpViewModelFactory(
    private val shopifyRepo: ShopifyRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(shopifyRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}