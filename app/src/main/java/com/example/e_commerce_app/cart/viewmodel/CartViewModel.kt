    package com.example.e_commerce_app.cart.viewmodel

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.e_commerce_app.model.cart.CartResponse
    import com.example.e_commerce_app.model.cart.DraftOrderResponse
    import com.example.e_commerce_app.model.repo.ShopifyRepo
    import com.example.e_commerce_app.util.ApiState
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch

    class CartViewModel(private val repo: ShopifyRepo) : ViewModel() {



        private val _draftOrderState = MutableStateFlow<ApiState<DraftOrderResponse>>(ApiState.Loading())
        val draftOrderState: StateFlow<ApiState<DraftOrderResponse>> = _draftOrderState


        fun getProductsFromDraftOrder(draftFavoriteId: Long) {
            viewModelScope.launch {
                _draftOrderState.value = ApiState.Loading()
                try {
                    val result = repo.getProductsIdForDraftFavorite(draftFavoriteId)
                    _draftOrderState.value = result
                } catch (e: Exception) {
                    _draftOrderState.value = ApiState.Error("Exception occurred: ${e.message}")
                }
            }
        }



    }
