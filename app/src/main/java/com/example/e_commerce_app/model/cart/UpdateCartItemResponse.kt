package com.example.e_commerce_app.model.cart

import com.google.gson.annotations.SerializedName

data class UpdateCartItemRequest(
    val line_item: LineItemUpdate
)

data class LineItemUpdate(
    val quantity: Int
)



data class UpdateCartItemResponse(
    @SerializedName("cart")
    val cart: Cart
)

