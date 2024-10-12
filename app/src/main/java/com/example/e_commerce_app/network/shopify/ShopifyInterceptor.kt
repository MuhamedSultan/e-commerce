package com.example.e_commerce_app.network.shopify

import okhttp3.Interceptor
import okhttp3.Response

class ShopifyInterceptor(private val accessToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Shopify-Access-Token", accessToken)  // Add Access Token
            .build()
        return chain.proceed(request)
    }
}
