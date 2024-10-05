package com.example.e_commerce_app.model.repo

import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.flow.Flow

interface ShopifyRepo {
    suspend fun getAllBrands(): ApiState<SmartCollectionResponse>

//    fun getAllCartProducts(): Flow<CartResponse>
    //suspend fun getDraftIds(customerId: String): Flow<ApiState<List<String>>>
}