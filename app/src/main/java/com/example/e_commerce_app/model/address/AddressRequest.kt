package com.example.e_commerce_app.model.address

import com.google.gson.annotations.SerializedName

data class AddressRequest(
    val address: testAdd
)
data class testAdd(
    val address1: String,
    val address2: String?,
    val city: String,
    @SerializedName("first_name")
    val first_name: String,
    @SerializedName("last_name")
    val last_name: String,
    val phone: String?,
    val province: String,
    val country: String,
    val default : Boolean
)
data class AddressReqModel(
    val address1: String,
    val address2: String?,
    val city: String,
    val company: String?,
    val first_name: String,
    val last_name: String,
    val phone: String?,
    val province: String,
    val country: String,
    val zip: String,
    val name: String,
    val province_code: String,
    val country_code: String,
    val country_name: String
)
