package com.example.e_commerce_app.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel (private val repo: ShopifyRepo) : ViewModel() {
//    private val _draftIds : MutableStateFlow<ApiState<List<String>>> =
//        MutableStateFlow(ApiState.Loading())
//    val draftIds: StateFlow<ApiState<List<String>>> = _draftIds
//
//    fun getDraftFavoriteId(customerId: Long) {
//        viewModelScope.launch {
//            repo.getDraftIds(customerId.toString()).collect{
//                _draftIds.value=it
//
//            }
//        }
//    }
}