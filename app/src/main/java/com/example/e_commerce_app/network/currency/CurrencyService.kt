package com.example.e_commerce_app.network.currency

import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") accessKey: String = "329a2e684f177cf1182195c4a5de5d42"
    ): CurrencyResponse
}