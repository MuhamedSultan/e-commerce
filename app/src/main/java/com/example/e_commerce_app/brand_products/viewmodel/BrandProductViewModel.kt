package com.example.e_commerce_app.brand_products.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BrandProductViewModel(private val shopifyRepo: ShopifyRepo) : ViewModel() {

    private val _brandProductResult: MutableStateFlow<ApiState<ProductResponse>> =
        MutableStateFlow(ApiState.Loading())
    val brandProductResult: StateFlow<ApiState<ProductResponse>> = _brandProductResult

    fun getBrandProducts(brandName: String) = viewModelScope.launch(Dispatchers.IO) {
        val result=shopifyRepo.getBrandProducts(brandName)
        _brandProductResult.value=result
    }
}