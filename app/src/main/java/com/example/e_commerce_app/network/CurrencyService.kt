package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") accessKey: String="21fee34b15e4320b6811a41c1e0972d5",
    ): CurrencyResponse
}