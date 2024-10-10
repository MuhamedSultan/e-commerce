package com.example.e_commerce_app.orders_details.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.order_details.OrderDetailsResponse
import com.example.e_commerce_app.model.orders.CustomerOrders
import com.example.e_commerce_app.model.orders.Order
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderDetailsViewModel(private val shopifyRepo: ShopifyRepo) : ViewModel() {
    private val _orderDetailsResult: MutableStateFlow<ApiState<OrderDetailsResponse>> =
        MutableStateFlow(ApiState.Loading())
    val orderDetailsResult: StateFlow<ApiState<OrderDetailsResponse>> = _orderDetailsResult

    private val _currencyRates: MutableStateFlow<ApiState<CurrencyResponse>> =
        MutableStateFlow(ApiState.Loading())
    val currencyRates: StateFlow<ApiState<CurrencyResponse>> = _currencyRates

    fun getOrderDetailsById(orderId: Long) = viewModelScope.launch {
        val result = shopifyRepo.getOrderDetailsByID(orderId)
        _orderDetailsResult.value = result
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