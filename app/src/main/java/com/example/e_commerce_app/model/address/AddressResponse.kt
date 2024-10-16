package com.example.e_commerce_app.model.address

import com.google.gson.annotations.SerializedName
data class AddressResponse(
    val customer_address:AddressResponseModel
)
data class AddressResponseModel(
    @SerializedName("address1")
    val address1: String?,
    @SerializedName("address2")
    val address2: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("company")
    val company: String? = null,
    @SerializedName("country")
    val country: String?,
    @SerializedName("country_code")
    val countryCode: String? = null,
    @SerializedName("country_name")
    val countryName: String? = null,
    @SerializedName("customer_id")
    val customerId: Long,
    @SerializedName("default")
    var default: Boolean? = null,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("id")
    val id: Long,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("province")
    val province: String?,
    @SerializedName("province_code")
    val provinceCode: String? = null,
    @SerializedName("zip")
    val zip: String? = null
)
