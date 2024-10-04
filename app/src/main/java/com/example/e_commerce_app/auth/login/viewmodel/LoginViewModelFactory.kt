package com.example.e_commerce_app.auth.login.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.network.RemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModelFactory(
    private val shopifyRepo: ShopifyRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(shopifyRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}