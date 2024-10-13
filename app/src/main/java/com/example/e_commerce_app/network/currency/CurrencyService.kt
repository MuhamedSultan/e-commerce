package com.example.e_commerce_app.network.currency

import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") accessKey: String = "d1ca3814684aca001cc6eb6f243c9728",//c25cb498f423999197dda5d408fd7a94
    ): CurrencyResponse
}