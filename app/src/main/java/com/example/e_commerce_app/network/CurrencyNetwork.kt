package com.example.e_commerce_app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CurrencyNetwork {
    private const val BASE_URL = "https://api.exchangeratesapi.io/v1/"


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val currencyService: CurrencyService by lazy {
        retrofit.create(CurrencyService::class.java)
    }


}