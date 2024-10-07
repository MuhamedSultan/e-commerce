package com.example.e_commerce_app.product_details.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.DraftOrderResponse
import com.example.e_commerce_app.model.cart.UpdateCartItemRequest
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val repository: ShopifyRepo,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _productState = MutableStateFlow<ApiState<Product>>(ApiState.Loading())
    val productState: StateFlow<ApiState<Product>> = _productState

    private val _cartState = MutableStateFlow<ApiState<Unit>>(ApiState.Loading())
    val cartState: StateFlow<ApiState<Unit>> = _cartState

    private val _draftOrderState = MutableStateFlow<ApiState<DraftOrderResponse>>(ApiState.Loading())
    val draftOrderState: StateFlow<ApiState<DraftOrderResponse>> = _draftOrderState




    fun fetchProductDetails(productId: Long) {
        viewModelScope.launch {
            _productState.value = ApiState.Loading()

            val result = repository.getProductById(productId)

            _productState.value = result
        }
    }

    fun addToFavorite(product: Product) {
        viewModelScope.launch {
            val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
            if (shopifyCustomerId != null) {
                val productWithShopifyId = product.copy(shopifyCustomerId = shopifyCustomerId)
                repository.addToFavorite(productWithShopifyId)
            } else {
                Log.e("FavoriteViewModel", "Shopify Customer ID is null. Cannot add to favorites.")
            }
        }
    }




    fun addToCart(draftOrderRequest: DraftOrderRequest) {
        viewModelScope.launch {
            _draftOrderState.value = ApiState.Loading()
            val result = repository.backUpDraftFavorite(draftOrderRequest,123)
            _draftOrderState.value = result

            when (result) {
                is ApiState.Success -> {
                    Log.d("ProductDetailsViewModel", "Draft order created successfully")
                }
                is ApiState.Error -> {
                    Log.e("ProductDetailsViewModel", "Error creating draft order: ${result.message}")
                }
                // Handle loading state if needed
                is ApiState.Loading -> TODO()
            }
        }
    }



    // Function to fetch product IDs from the draft order (getting products in cart)
    fun getProductsFromDraftOrder(draftFavoriteId: Long) {
        viewModelScope.launch {
            _draftOrderState.value = ApiState.Loading()
            val result = repository.getProductsIdForDraftFavorite(draftFavoriteId)
            _draftOrderState.value = result
        }
    }



    // Function to update (back up) the draft order
    fun updateDraftOrder(draftOrderRequest: DraftOrderRequest, draftFavoriteId: Long) {
        viewModelScope.launch {
            _draftOrderState.value = ApiState.Loading()
            val result = repository.backUpDraftFavorite(draftOrderRequest, draftFavoriteId)
            _draftOrderState.value = result
        }
    }




}
