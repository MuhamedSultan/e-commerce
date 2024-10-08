package com.example.e_commerce_app.model.order_details

data class BillingAddress(
    val address1: String,
    val address2: Any,
    val city: String,
    val company: Any,
    val country: String,
    val country_code: String,
    val first_name: String,
    val last_name: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val phone: String,
    val province: Any,
    val province_code: Any,
    val zip: String
)