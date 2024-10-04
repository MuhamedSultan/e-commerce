package com.example.e_commerce_app.model.user

data class CustomerEntity(
    val id: Long,
    val first_name: String,
    val email: String,
    val verified_email: Boolean,
)
