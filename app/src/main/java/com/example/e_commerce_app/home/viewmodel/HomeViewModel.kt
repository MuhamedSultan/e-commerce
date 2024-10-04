package com.example.e_commerce_app.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun getAllBrands() = viewModelScope.launch(Dispatchers.IO) {
        val result = shopifyRepo.getAllBrands().data
        _brandsResult.value = ApiState.Success(result!!)
    }
}