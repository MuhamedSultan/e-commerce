package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.util.ApiState
import com.example.e_commerce_app.util.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class RemoteDataSourceImpl : RemoteDataSource {
    private val firebaseFireStore = FirebaseFirestore.getInstance()
    override suspend fun getAllBrands(): ApiState<SmartCollectionResponse> {
        return try {
            val response = Network.shopifyService.getAllBrands()
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error(e.message.toString())
        }
    }

    /*override fun getAllCartProducts(): Flow<CartResponse> {
        return Network.shopifyService.getAllCartProducts()
    }

    override suspend fun getDraftIds(customerId: String): Task<DocumentSnapshot> {

        return   firebaseFireStore.collection(Constants.collectionPath)
            .document(customerId).get()
    }*/

}