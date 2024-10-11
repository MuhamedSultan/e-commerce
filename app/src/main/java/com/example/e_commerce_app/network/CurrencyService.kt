package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") accessKey: String="c5bec40815ad06c0ce20f8b56b690ebf",
    ): CurrencyResponse
}