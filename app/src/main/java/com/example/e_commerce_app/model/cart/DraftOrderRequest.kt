package com.example.e_commerce_app.model.cart

import com.example.e_commerce_app.model.address.testAdd
import com.google.gson.annotations.SerializedName

data class DraftOrderRequest(
    @SerializedName("draft_order")
    val draftOrder: DraftOrder
)

data class DraftOrder(
    @SerializedName("line_items")
    val lineItems: MutableList<LineItems> = mutableListOf(LineItems()),
    @SerializedName("applied_discount")
    var appliedDiscount: AppliedDiscount?=null,
    val customer: CustomerId,
    @SerializedName("shipping_address")
    var shippingAddress : testAdd? = null,
    @SerializedName("billing_address")
    var billingAddress : testAdd? = null,
    @SerializedName("use_customer_default_address")
    val useCustomerDefaultAddress: Boolean,
    var note : String = ""
)

data class LineItems(
    val title: String = "mn",
    val price: String = "0.00",
    var quantity: Int = 1,
    @SerializedName("product_id")
    val productId: String = "12",
    @SerializedName("variant_id")
    val variantId: String? = null,
)

data class AppliedDiscount(
    val description: String,
    val id: Long,
    @SerializedName("value_type")
    val valueType: String,
    val value: String,
    val title: String
)

data class CustomerId(
    val id: Long
)