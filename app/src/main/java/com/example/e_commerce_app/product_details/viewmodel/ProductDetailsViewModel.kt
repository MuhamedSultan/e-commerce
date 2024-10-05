package com.example.e_commerce_app.product_details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailsViewModel(private val repository: ShopifyRepo) : ViewModel() {
    private val _productState = MutableStateFlow<ApiState<Product>>(ApiState.Loading())
    val productState: StateFlow<ApiState<Product>> = _productState


    fun fetchProductDetails(productId: Long) {
        viewModelScope.launch {
            _productState.value = ApiState.Loading()

            val result = repository.getProductById(productId)

            _productState.value = result
        }
    }


    fun addToFavorite(product: Product) {
        viewModelScope.launch {
            repository.addToFavorite(product)
        }
    }
}
