package com.example.e_commerce_app.map.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.model.address.AddressRequest
import com.example.e_commerce_app.model.address.AddressResponse
import com.example.e_commerce_app.model.address.AddressesResponse
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.DraftOrderResponse
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

    private val _draftOrderState = MutableStateFlow<ApiState<DraftOrderResponse>>(ApiState.Loading())
    val draftOrderState: StateFlow<ApiState<DraftOrderResponse>> = _draftOrderState


    fun getAllAddresses(customerId:String) = viewModelScope.launch(Dispatchers.IO) {
        val result=repo.getAllAddresses(customerId)
        _addressesResult.value=result
    }

    fun insertAddress(customerId: String, addressRequest: AddressRequest) = viewModelScope.launch(Dispatchers.IO) {
        val result=repo.insertAddress(customerId.toLong(),addressRequest)
        _insertAddressesResult.value=result
    }

    fun addAddressToDraftOrder(draftOrderRequest: DraftOrderRequest, draftOrderId: Long)
    = viewModelScope.launch(Dispatchers.IO) {
        _draftOrderState.value = ApiState.Loading()
        val result = repo.backUpDraftFavorite(draftOrderRequest,draftOrderId)
        _draftOrderState.value = result

        when (result) {
            is ApiState.Success -> {
                Log.d("TAG", "Address Added successfully")
                Log.i("TAG", "Add Response: ${result.data?.draft_order}")
            }
            is ApiState.Error -> {
                Log.e(
                    "TAG",
                    "Error Adding Address to draft order: ${result.message}"
                )
            }
            // Handle loading state if needed
            is ApiState.Loading -> TODO()
        }

    }

    fun deleteAddress(addressId: Long) = viewModelScope.launch(Dispatchers.IO) {
        Log.i("TAG", "deleteAddress: in vm")
        var customerId = SharedPrefsManager.getInstance().getShopifyCustomerId()?.toLong()?:0
        Log.i("TAG", "customerId: $customerId\naddressId : $addressId")
        repo.deleteAddress(customerId = customerId,addressId = addressId)
    }
}