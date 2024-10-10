package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET("latest")
    suspend fun getExchangeRates(
        @Query("access_key") accessKey: String="54a08e7a53416df4d065f28c95affe32",
    ): CurrencyResponse
}