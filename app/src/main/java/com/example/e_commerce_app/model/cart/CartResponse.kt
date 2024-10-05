package com.example.e_commerce_app.model.cart

import com.google.gson.annotations.SerializedName

data class CartResponse(
    val carts: List<Cart>
)

data class Cart(
    @SerializedName("id")
    val id: String,
    @SerializedName("line_items")
    val lineItems: List<LineItem>
)

data class LineItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("total_price")
    val totalPrice: String
)

data class DeleteProductResponse(
    val success: Boolean // This can be customized based on Shopify's actual response
)