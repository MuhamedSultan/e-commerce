package com.example.e_commerce_app.network.paypal


import com.example.e_commerce_app.model.payment.AccessTokenResponse
import com.example.e_commerce_app.model.payment.CaptureRequest
import com.example.e_commerce_app.model.payment.OrderRequest
import com.example.e_commerce_app.model.payment.OrderResponse
import retrofit2.http.*



interface PayPalService {

    // Fetch Access Token
    @FormUrlEncoded
    @POST("v1/oauth2/token")
    suspend fun fetchAccessToken(
        @Header("Authorization") authorization: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): AccessTokenResponse

    // Create an Order
    @POST("v2/checkout/orders")
    suspend fun createOrder(
        @Header("Authorization") authorization: String,
        @Header("PayPal-Request-Id") requestId: String,
        @Body orderRequest: OrderRequest
    ): OrderResponse

    // Capture an Order
    @POST("v2/checkout/orders/{order_id}/capture")
    suspend fun captureOrder(
        @Header("Authorization") authHeader: String,
        @Path("order_id") orderId: String,
        @Body captureRequest: CaptureRequest
    ): OrderResponse
}
///@POST("v2/checkout/orders/{order_id}/capture")
//suspend fun captureOrder(
//    @Header("Authorization") authHeader: String,
//    @Header("PayPal-Partner-Attribution-Id") partnerId: String,
//    @Header("PayPal-Auth-Assertion") authAssertion: String,
//    @Path("order_id") orderId: String,
//    @Body captureRequest: CaptureRequest? = null // Send null or an empty object
//): OrderResponse