package com.example.e_commerce_app.brand_products.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
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

    private val _filteredProducts: MutableStateFlow<List<Product>> = MutableStateFlow(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts

    private var allProducts: List<Product> = emptyList()


    private val _currencyRates: MutableStateFlow<ApiState<CurrencyResponse>> =
        MutableStateFlow(ApiState.Loading())
    val currencyRates: StateFlow<ApiState<CurrencyResponse>> = _currencyRates

    fun getBrandProducts(brandName: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = shopifyRepo.getBrandProducts(brandName)
        _brandProductResult.value = result
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


    fun filterProducts(query: String) {
        if (query.isEmpty()) {
            _filteredProducts.value = allProducts
            return
        }

        val filtered = allProducts.filter { product ->
            product.title.contains(query, ignoreCase = true)
        }

        _filteredProducts.value = filtered
    }
    fun setAllProducts(products: List<Product>) {
        allProducts = products
        _filteredProducts.value = allProducts
    }
    fun fetchCurrencyRates() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val rates = shopifyRepo.exchangeRate()
                _currencyRates.value = rates
            } catch (e: Exception) {
                Log.e("productInfo", "Failed to fetch currency rates: ${e.message}")
            }

        }
    }
}