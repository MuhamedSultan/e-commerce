package com.example.e_commerce_app.orders_details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.order_details.OrderDetailsResponse
import com.example.e_commerce_app.model.orders.CustomerOrders
import com.example.e_commerce_app.model.orders.Order
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderDetailsViewModel(private val shopifyRepo: ShopifyRepo) : ViewModel() {
    private val _orderDetailsResult: MutableStateFlow<ApiState<OrderDetailsResponse>> =
        MutableStateFlow(ApiState.Loading())
    val orderDetailsResult: StateFlow<ApiState<OrderDetailsResponse>> = _orderDetailsResult

    fun getOrderDetailsById(orderId: Long) = viewModelScope.launch {
        val result = shopifyRepo.getOrderDetailsByID(orderId)
        _orderDetailsResult.value = result
    }
}