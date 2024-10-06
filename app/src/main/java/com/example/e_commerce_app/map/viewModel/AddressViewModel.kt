package com.example.e_commerce_app.map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun getAllAddresses() = viewModelScope.launch(Dispatchers.IO) {
        var sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val customerId :String= repo.getCustomerIdSHP()
        val result=repo.getAllAddresses(customerId)
        _addressesResult.value=result
    }
}