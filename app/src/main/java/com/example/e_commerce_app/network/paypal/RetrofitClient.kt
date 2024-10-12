package com.example.e_commerce_app.network.paypal


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api-m.sandbox.paypal.com/"

    val instance: PayPalService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)

            .addConverterFactory(GsonConverterFactory.create())

            .build()

        retrofit.create(PayPalService::class.java)
    }
}
