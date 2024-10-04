package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.util.ApiState

interface RemoteDataSource {
    suspend fun getAllBrands():ApiState<SmartCollectionResponse>
}