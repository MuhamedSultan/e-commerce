package com.example.e_commerce_app.network.currency

import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") accessKey: String = "cc349888d16649220ac8b2636c04b2a6",
    ): CurrencyResponse
}