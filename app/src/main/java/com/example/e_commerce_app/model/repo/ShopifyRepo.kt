package com.example.e_commerce_app.model.repo

import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.util.ApiState

interface ShopifyRepo {
    suspend fun getAllBrands(): ApiState<SmartCollectionResponse>
    suspend fun getRandomProducts(): ApiState<ProductResponse>

}