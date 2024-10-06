package com.example.e_commerce_app.model.address

import com.google.gson.annotations.SerializedName

data class AddressesResponse(
    @SerializedName("addresses")
    val addressResponses: List<AddressResponse>
)