package com.example.e_commerce_app.model.repo

import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.util.ApiState

interface ShopifyRepo {
    suspend fun getAllBrands(): ApiState<SmartCollectionResponse>
    suspend fun getRandomProducts(): ApiState<ProductResponse>



    suspend fun signInUser(email: String, password: String): ApiState<UserData>
    suspend fun registerUser(userData: UserData): ApiState<Unit>
    suspend fun createShopifyCustomer(customerRequest: CustomerRequest): ApiState<Unit>
    suspend fun getProductById(productId: Long): ApiState<Product>


}