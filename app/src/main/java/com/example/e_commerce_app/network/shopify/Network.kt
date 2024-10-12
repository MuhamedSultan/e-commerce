package com.example.e_commerce_app.network.shopify

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Network {

    private const val BASE_URL = "https://android-alex-team3.myshopify.com/admin/api/2024-01/"
    private const val ACCESS_TOKEN = "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"

    private val client = OkHttpClient.Builder()
        .addInterceptor(ShopifyInterceptor(ACCESS_TOKEN))
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val shopifyService: ShopifyService by lazy {
        retrofit.create(ShopifyService::class.java)
    }
}
