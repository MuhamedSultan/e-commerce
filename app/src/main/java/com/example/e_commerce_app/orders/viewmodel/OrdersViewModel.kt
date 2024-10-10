package com.example.e_commerce_app.orders.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.orders.CustomerOrders
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(private val shopifyRepo: ShopifyRepo) : ViewModel() {

    private val _ordersResult: MutableStateFlow<ApiState<CustomerOrders>> =
        MutableStateFlow(ApiState.Loading())
    val ordersResult: StateFlow<ApiState<CustomerOrders>> = _ordersResult
    private val _currencyRates: MutableStateFlow<ApiState<CurrencyResponse>> =
        MutableStateFlow(ApiState.Loading())
    val currencyRates: StateFlow<ApiState<CurrencyResponse>> = _currencyRates

    fun getCustomerOrders(customerId: Long) = viewModelScope.launch {
        val result = shopifyRepo.getCustomerOrders(customerId)
        _ordersResult.value = result
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