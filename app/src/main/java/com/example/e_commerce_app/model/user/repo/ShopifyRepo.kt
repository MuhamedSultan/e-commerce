package com.example.e_commerce_app.model.user.repo

import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.util.ApiState

interface ShopifyRepo {
    suspend fun getAllBrands(): ApiState<SmartCollectionResponse>

}