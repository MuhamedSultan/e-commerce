package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.cart.DeleteProductResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.CustomerResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface ShopifyService {

    // POST request to create a customer in Shopify
    @POST("customers.json")
    suspend fun createCustomer(
        @Body customerRequest: CustomerRequest
    ): Response<CustomerResponse>

    @GET("carts.json")
    fun getAllCartProducts(): Flow<CartResponse>

    // New method to delete a specific product from a cart
    @DELETE("carts/{cart_id}/line_items/{line_item_id}.json")
    suspend fun deleteCartProduct(
        @Path("cart_id") cartId: String,
        @Path("line_item_id") lineItemId: String
    ): Response<DeleteProductResponse>

    @GET("smart_collections.json")
    suspend fun getAllBrands():SmartCollectionResponse
}
