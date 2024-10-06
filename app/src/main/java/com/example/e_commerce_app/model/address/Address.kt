package com.example.e_commerce_app.model.address

data class Address (
    val userId : Int,
    val lon : Double,
    val lat : Double,
    val country: String,
    val address: String,
    val phone : String
)
