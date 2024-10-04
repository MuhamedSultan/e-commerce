package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.util.ApiState

class RemoteDataSourceImpl : RemoteDataSource {
    override suspend fun getAllBrands(): ApiState<SmartCollectionResponse> {
        return try {
            val response = Network.shopifyService.getAllBrands()
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error(e.message.toString())
        }
    }
}