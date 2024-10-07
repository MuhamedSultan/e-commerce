package com.example.e_commerce_app.map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.address.AddressResponse
import com.example.e_commerce_app.model.address.AddressesResponse
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressViewModel(val repo: ShopifyRepo) : ViewModel() {
    private val _addressesResult: MutableStateFlow<ApiState<AddressesResponse>> =
        MutableStateFlow(ApiState.Loading())
    val addressesResult: StateFlow<ApiState<AddressesResponse>> = _addressesResult

     private val _insertAddressesResult: MutableStateFlow<ApiState<AddressResponse>> =
        MutableStateFlow(ApiState.Loading())
    val insertAddressesResult: StateFlow<ApiState<AddressResponse>> = _insertAddressesResult





    fun getAllAddresses(customerId:String) = viewModelScope.launch(Dispatchers.IO) {
        val result=repo.getAllAddresses(customerId)
        _addressesResult.value=result
    }

    fun insertAddress(addressResponse: AddressResponse) = viewModelScope.launch(Dispatchers.IO) {
        val result=repo.insertAddress(addressResponse)
        _insertAddressesResult.value=result
    }
}