package com.example.e_commerce_app.payment.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.cart.DraftOrderManager
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.model.cart.CustomerId
import com.example.e_commerce_app.model.cart.DraftOrder
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.DraftOrderResponse
import com.example.e_commerce_app.model.cart.LineItems
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(private val repo: ShopifyRepo) :ViewModel() {

    private val _creatingDraftOrder: MutableStateFlow<ApiState<DraftOrderResponse>> =
        MutableStateFlow(ApiState.Loading())
    val creatingDraftOrder: StateFlow<ApiState<DraftOrderResponse>> = _creatingDraftOrder


    fun CreateOrder(paymentPending: Boolean) =viewModelScope.launch (Dispatchers.IO){
        _creatingDraftOrder.value = ApiState.Loading()

        val shp = SharedPrefsManager.getInstance()
        var draftFavoriteId =shp.getDraftedOrderId() ?:0
        val result = repo.addOrderFromDraftOrder(draftFavoriteId , paymentPending)
        when (result) {
            is ApiState.Success -> {
                getDraftOrderSaveInShP()
                Log.d("TAG", "Order Added successfully")
                Log.i("TAG", "Add Response: ${result.data?.draft_order}")
            }
            is ApiState.Error -> {
                Log.e(
                    "TAG",
                    "Error getting draft order Data: ${result.message}"
                )
            }
            // Handle loading state if needed
            is ApiState.Loading -> TODO()
        }
    }
    fun getDraftOrderSaveInShP()=viewModelScope.launch (Dispatchers.IO){
        Log.i("TAG", "getDraftOrderSaveInShP: Started")
        val customerId = SharedPrefsManager.getInstance().getShopifyCustomerId()
        DraftOrderManager.destroy()
        Log.i("TAG", "customerId: $customerId")
        if (customerId!=null) {

            val draftOrderRequest = DraftOrderRequest(
                draftOrder = DraftOrder(
                    lineItems = mutableListOf(
                        LineItems(
                            title = "m",
                            price = "0.00",
                            quantity = 1,
                            productId = "12",
                            variantId = null
                        )
                    ),
                    appliedDiscount = null,
                    customer = CustomerId(id = customerId.toLong()),
                    useCustomerDefaultAddress = true
                )
            )
            val result = repo.createFavoriteDraft(draftOrderRequest)
            DraftOrderManager.init(draftOrderRequest)
            _creatingDraftOrder.value=result
        }
    }

}