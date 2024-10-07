package com.example.e_commerce_app.model.cart


data class DraftOrderRequest(
    val draft_order: DraftOrderDetailsRequest
)

data class CustomerDraftRequest(val id: Long)

data class DraftOrderDetailsRequest(
    val line_items: List<LineItems> =  listOf(LineItems()),
    val customer: CustomerDraftRequest,
    val note :String = "note"
)

data class LineItems(
    val quantity: Int=1,
    val price: String = "0.0",
    val title: String? = "m",
    val product_id: String = "",
    val variant_id: Long? =null
    )

