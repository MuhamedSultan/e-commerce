package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.CustomerResponse
import com.example.e_commerce_app.util.ApiState
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ShopifyService {

    // POST request to create a customer in Shopify
    @POST("customers.json")
    suspend fun createCustomer(
        @Body customerRequest: CustomerRequest
    ): Response<CustomerResponse>

    @GET("smart_collections.json")
    suspend fun getAllBrands():SmartCollectionResponse
}
