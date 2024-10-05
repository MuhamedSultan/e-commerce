package com.example.e_commerce_app.categories.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.custom_collection.CustomCollectionResponse
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

    fun getCategorise() = viewModelScope.launch(Dispatchers.IO) {
        val result = shopifyRepo.getCategories()
        _categoriesResult.value = result
    }

}