package com.example.e_commerce_app.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val shopifyRepo: ShopifyRepo) : ViewModel() {
    private val _brandsResult: MutableStateFlow<ApiState<SmartCollectionResponse>> =
        MutableStateFlow(ApiState.Loading())
    val brandsResult: StateFlow<ApiState<SmartCollectionResponse>> = _brandsResult

    private val _randProductsResult: MutableStateFlow<ApiState<ProductResponse>> =
        MutableStateFlow(ApiState.Loading())
    val randProductsResult: StateFlow<ApiState<ProductResponse>> = _randProductsResult

    fun getAllBrands() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val result = shopifyRepo.getAllBrands().data
            result?.let {
                _brandsResult.value = ApiState.Success(it)
            } ?: run {
                _brandsResult.value = ApiState.Error("No brands data found")
            }
        } catch (e: Exception) {
            _brandsResult.value = ApiState.Error(e.message ?: "Unknown error")
        }

    }

    fun getRandomProducts() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val result = shopifyRepo.getRandomProducts().data
            result?.let {
                _randProductsResult.value = ApiState.Success(it)
            } ?: run {
                _randProductsResult.value = ApiState.Error("No product data found")
            }
        } catch (e: Exception) {
            _randProductsResult.value = ApiState.Error(e.message ?: "Unknown error")
        }
    }
}