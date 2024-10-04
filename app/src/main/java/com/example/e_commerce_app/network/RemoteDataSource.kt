package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.util.ApiState

interface RemoteDataSource {

    suspend fun getAllBrands():ApiState<SmartCollectionResponse>

    suspend fun signInUser(email: String, password: String): ApiState<UserData>
    suspend fun registerUser(userData: UserData): ApiState<Unit>
    suspend fun createShopifyCustomer(customerRequest: CustomerRequest): ApiState<Unit>


    suspend fun getRandomProducts():ApiState<ProductResponse>
}