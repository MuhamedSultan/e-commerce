package com.example.e_commerce_app.categories.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.custom_collection.CustomCollectionResponse
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel(private val shopifyRepo: ShopifyRepo) : ViewModel() {

    private val _categoriesResult: MutableStateFlow<ApiState<CustomCollectionResponse>> =
        MutableStateFlow(ApiState.Loading())
    val categoriesResult: StateFlow<ApiState<CustomCollectionResponse>> = _categoriesResult

    private val _productsResult: MutableStateFlow<ApiState<ProductResponse>> =
        MutableStateFlow(ApiState.Loading())
    val productsResult: StateFlow<ApiState<ProductResponse>> = _productsResult

    fun getCategorise() = viewModelScope.launch(Dispatchers.IO) {
        val result = shopifyRepo.getCategories()
        _categoriesResult.value = result
    }

    fun getProductsOfSelectedCategory(collectionId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val result = shopifyRepo.getProductsOfSelectedCategory(collectionId)
        _productsResult.value = result
    }

    fun addProductToFavourite(product: Product, shopifyCustomerId: String) {
        viewModelScope.launch {
            val productWithShopifyId = product.copy(shopifyCustomerId = shopifyCustomerId)
            shopifyRepo.addToFavorite(productWithShopifyId)
        }
    }

    fun deleteProductFromFavourite(product: Product, shopifyCustomerId: String) {
        viewModelScope.launch {
            val productWithShopifyId = product.copy(shopifyCustomerId = shopifyCustomerId)
            shopifyRepo.removeFavorite(productWithShopifyId)
        }
    }

}