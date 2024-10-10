    package com.example.e_commerce_app.cart.viewmodel

    import android.util.Log
    import android.widget.Toast
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.e_commerce_app.db.SharedPrefsManager
    import com.example.e_commerce_app.model.cart.CartResponse
    import com.example.e_commerce_app.model.cart.DraftOrderRequest
    import com.example.e_commerce_app.model.cart.DraftOrderResponse
    import com.example.e_commerce_app.model.cart.PriceRuleResponse
    import com.example.e_commerce_app.model.repo.ShopifyRepo
    import com.example.e_commerce_app.util.ApiState
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch

    class CartViewModel(private val repo: ShopifyRepo) : ViewModel() {

        private val _allCouponsResult = MutableStateFlow<ApiState<PriceRuleResponse>>(ApiState.Loading())
        val allCouponsResult: StateFlow<ApiState<PriceRuleResponse>> = _allCouponsResult

        private val _draftOrderState = MutableStateFlow<ApiState<DraftOrderResponse>>(ApiState.Loading())
        val draftOrderState: StateFlow<ApiState<DraftOrderResponse>> = _draftOrderState

        fun UpdateDraftOrderProducts(draftOrderRequest: DraftOrderRequest , draftOrderId: Long)  =viewModelScope.launch (Dispatchers.IO){
                _draftOrderState.value = ApiState.Loading()
                val result = repo.backUpDraftFavorite(draftOrderRequest,draftOrderId)
                _draftOrderState.value = result
        }

        fun getProductsFromDraftOrder(draftFavoriteId: Long) =viewModelScope.launch (Dispatchers.IO){

            _draftOrderState.value = ApiState.Loading()
            val result = repo.getProductsIdForDraftFavorite(draftFavoriteId)
            _draftOrderState.value = result
            when (result) {
                is ApiState.Success -> {
                    Log.d("TAG", "get Draft Order data successfully")
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
        fun addCouponToDraftOrder(draftOrderRequest: DraftOrderRequest, draftOrderId: Long)
                = viewModelScope.launch(Dispatchers.IO) {
            _draftOrderState.value = ApiState.Loading()
            val result = repo.backUpDraftFavorite(draftOrderRequest,draftOrderId)
            _draftOrderState.value = result

            when (result) {
                is ApiState.Success -> {
                    Log.d("TAG", "Coupon Added To successfully")
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


        fun completeOrderForSultan() =viewModelScope.launch (Dispatchers.IO){
            val shp = SharedPrefsManager.getInstance()
            var draftFavoriteId =shp.getDraftedOrderId() ?:0
            val result = repo.addOrderFromDraftOrder(draftFavoriteId , true)
            when (result) {
                is ApiState.Success -> {
                    shp.setDraftedOrderId(0L)
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

        fun getAllCoupons() =viewModelScope.launch (Dispatchers.IO){
            _draftOrderState.value = ApiState.Loading()
            val result = repo.getAllCoupons()
            _allCouponsResult.value = result
            when (result) {
                is ApiState.Success -> {
                    Log.d("TAG", "Coupon Getted successfully")
                    Log.i("TAG", "Add Response: ${result.data?.price_rules}")
                }
                is ApiState.Error -> {
                    Log.e(
                        "TAG",
                        "Error getting Coupons: ${result.message}"
                    )
                }
                // Handle loading state if needed
                is ApiState.Loading -> TODO()
            }

        }


    }
