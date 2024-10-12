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
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
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

    private val _draftOrderState =
        MutableStateFlow<ApiState<DraftOrderResponse>>(ApiState.Loading())
    val draftOrderState: StateFlow<ApiState<DraftOrderResponse>> = _draftOrderState

    private val _currencyRates: MutableStateFlow<ApiState<CurrencyResponse>> =
        MutableStateFlow(ApiState.Loading())
    val currencyRates: StateFlow<ApiState<CurrencyResponse>> = _currencyRates

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
                _randProductsResult.value = ApiState.Success(result)
                originalProducts = result.products
                _filteredProducts.value = emptyList()
            } ?: run {
                _randProductsResult.value = ApiState.Error("No Products data found")

            }
        } catch (e: Exception) {
            _randProductsResult.value = ApiState.Error(e.message ?: "Unknown error")
        }


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


    fun getDraftOrderSaveInShP(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        Log.i("TAG", "getDraftOrderSaveInShP: Started")
        SharedPrefsManager.init(context)
        val customerId = SharedPrefsManager.getInstance().getShopifyCustomerId()
        Log.i("TAG", "customerId: $customerId")
        if (customerId != null) {

            val draftOrderRequest = DraftOrderRequest(
                draftOrder = DraftOrder(
                    lineItems = mutableListOf(
                        LineItems(
                            title = "m",
                            price = "0.00",
                            quantity = 1,
                            productId = "12",
                            variantId = null
                        )
                    ),
                    appliedDiscount = null,
                    customer = CustomerId(id = customerId.toLong()),
                    useCustomerDefaultAddress = false
                )
            )
            val result = shopifyRepo.createFavoriteDraft(draftOrderRequest)
            DraftOrderManager.init(draftOrderRequest)
            _creatingDraftOrder.value = result
        }
    }

    // Function to fetch product IDs from the draft order (getting products in cart)
    fun getProductsFromDraftOrder(draftFavoriteId: Long) = viewModelScope.launch(Dispatchers.IO) {

        _draftOrderState.value = ApiState.Loading()
        val result = shopifyRepo.getProductsIdForDraftFavorite(draftFavoriteId)
        _draftOrderState.value = result
        when (result) {
            is ApiState.Success -> {
                Log.d("TAG", "get Draft Order data successfully")
                Log.i("TAG", "Add Response: ${result.data?.draft_order}")
            }

            is ApiState.Error -> {
                Log.e(
                    "TAG",
                    "Error getting draft order Data: ${result.message}"
                )
            }
            // Handle loading state if needed
            is ApiState.Loading -> TODO()
        }

    }

    fun deleteProductFromFavourite(product: Product, shopifyCustomerId: String) {
        viewModelScope.launch {
            val productWithShopifyId = product.copy(shopifyCustomerId = shopifyCustomerId)
            shopifyRepo.removeFavorite(productWithShopifyId)
        }
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



