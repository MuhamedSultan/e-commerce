package com.example.e_commerce_app.home.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.cart.DraftOrderManager
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.model.cart.CustomerId
import com.example.e_commerce_app.model.cart.DraftOrder
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.DraftOrderResponse
import com.example.e_commerce_app.model.cart.LineItems
import com.example.e_commerce_app.model.product.Product
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

    private val _filteredProducts: MutableStateFlow<List<Product>> = MutableStateFlow(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts
    var originalProducts: List<Product> = emptyList()

    private val _creatingDraftOrder: MutableStateFlow<ApiState<DraftOrderResponse>> =
        MutableStateFlow(ApiState.Loading())
    val creatingDraftOrder: StateFlow<ApiState<DraftOrderResponse>> = _creatingDraftOrder


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
        val result = shopifyRepo.getRandomProducts().data
        _randProductsResult.value = ApiState.Success(result!!)

        originalProducts = result.products
        _filteredProducts.value = emptyList()

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


    fun filterProducts(query: String) {
        if (query.isEmpty()) {
            _filteredProducts.value = originalProducts
        } else {
            val filteredList = originalProducts.filter { product ->
                product.title.contains(query, ignoreCase = true)
            }
            _filteredProducts.value = filteredList
        }
    }




    fun addProductToFavourite(product: Product, shopifyCustomerId: String) {
        viewModelScope.launch {
            val productWithShopifyId = product.copy(shopifyCustomerId = shopifyCustomerId)
            shopifyRepo.addToFavorite(productWithShopifyId)
        }
    }


    fun getDraftOrderSaveInShP(context: Context)=viewModelScope.launch (Dispatchers.IO){
        Log.i("TAG", "getDraftOrderSaveInShP: Started")
        SharedPrefsManager.init(context)
        val customerId = SharedPrefsManager.getInstance().getShopifyCustomerId()
        Log.i("TAG", "customerId: $customerId")
        if (customerId!=null) {

            val draftOrderRequest = DraftOrderRequest(
                draftOrder = DraftOrder(
                    lineItems = mutableListOf(
                        LineItems(
                            title = "m",
                            price = "10.00",
                            quantity = 1,
                            productId = "12",
                            variantId = null
                        )
                    ),
                    appliedDiscount = null,
                    customer = CustomerId(id = 8936192246075),
                    useCustomerDefaultAddress = true
                )
            )
            val result = shopifyRepo.createFavoriteDraft(draftOrderRequest)
            DraftOrderManager.init(draftOrderRequest.draftOrder)
            _creatingDraftOrder.value=result
        }
    }


    fun deleteProductFromFavourite(product: Product, shopifyCustomerId: String) {
        viewModelScope.launch {
            val productWithShopifyId = product.copy(shopifyCustomerId = shopifyCustomerId)
            shopifyRepo.removeFavorite(productWithShopifyId)
        }
    }

}



