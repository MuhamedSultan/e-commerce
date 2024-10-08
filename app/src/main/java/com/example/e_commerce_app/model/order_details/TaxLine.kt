package com.example.e_commerce_app.model.order_details

data class TaxLine(
    val channel_liable: Boolean,
    val price: String,
    val price_set: PriceSetX,
    val rate: Double,
    val title: String
)