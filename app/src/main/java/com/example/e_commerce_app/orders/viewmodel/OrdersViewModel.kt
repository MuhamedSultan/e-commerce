package com.example.e_commerce_app.orders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.orders.CustomerOrders
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(private val shopifyRepo: ShopifyRepo) : ViewModel() {

    private val _ordersResult: MutableStateFlow<ApiState<CustomerOrders>> =
        MutableStateFlow(ApiState.Loading())
    val ordersResult: StateFlow<ApiState<CustomerOrders>> = _ordersResult

    fun getCustomerOrders(customerId: Long) = viewModelScope.launch {
        val result = shopifyRepo.getCustomerOrders(customerId)
        _ordersResult.value = result
    }
}