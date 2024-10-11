package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") accessKey: String = "0e3398f872eb1e72ece0cea4f8cdb675",
    ): CurrencyResponse
}