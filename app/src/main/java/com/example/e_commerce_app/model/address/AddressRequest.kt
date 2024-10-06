package com.example.e_commerce_app.model.address

import com.google.gson.annotations.SerializedName

data class AddressRequest(
    @SerializedName("customer_address")
    val customerAddressResponse: AddressResponse
)
