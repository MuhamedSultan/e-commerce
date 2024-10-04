package com.example.e_commerce_app.auth.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.model.user.repo.ShopifyRepo
import com.example.e_commerce_app.network.RemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpViewModelFactory(
    private val shopifyRepo: ShopifyRepo // Change here
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(shopifyRepo) as T // Pass shopifyRepo
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}