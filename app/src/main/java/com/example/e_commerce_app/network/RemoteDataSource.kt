package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.util.ApiState
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun getAllBrands():ApiState<SmartCollectionResponse>
//    fun getAllCartProducts(): Flow<CartResponse>
//    suspend fun getDraftIds(customerId: String): Task<DocumentSnapshot>
}