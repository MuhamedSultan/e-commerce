package com.example.e_commerce_app.payment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.model.repo.ShopifyRepo

class PaymentViewModelFactory (private val repo: ShopifyRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}