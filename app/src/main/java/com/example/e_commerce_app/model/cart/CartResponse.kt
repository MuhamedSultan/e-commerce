package com.example.e_commerce_app.model.cart

data class CartResponse(
    val carts: List<Cart>
)

data class Cart(
    val id: String,
    val line_items: List<LineItem>
)

data class LineItem(
    val id: String,
    val title: String,
    val quantity: Int,
    val price: String
)

data class DeleteProductResponse(
    val success: Boolean // This can be customized based on Shopify's actual response
)