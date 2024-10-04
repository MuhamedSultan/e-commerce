package com.example.e_commerce_app.model.repo

import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.network.RemoteDataSource
import com.example.e_commerce_app.util.ApiState

class ShopifyRepoImpl(private val remoteDataSource: RemoteDataSource) : ShopifyRepo {
    override suspend fun getAllBrands(): ApiState<SmartCollectionResponse> {
        return remoteDataSource.getAllBrands()
    }

}