package com.example.e_commerce_app.model.cart
data class DraftOrderRequest(
    val draft_order: DraftOrder
)

data class DraftOrder(
    val line_items: List<LineItems>,
    val applied_discount: AppliedDiscount?=null,
    val customer: CustomerId,
    val use_customer_default_address: Boolean = true
)

data class LineItems(
    val title: String,
    val price: String,
    val quantity: Int
)

data class AppliedDiscount(
    val description: String,
    val value_type: String,
    val value: String,
    val amount: String,
    val title: String
)

data class CustomerId(
    val id: Long
)


/*
data class DraftOrderRequest(
    val draft_order: DraftOrderDetailsRequest
)

data class CustomerDraftRequest(val id: Long)

data class DraftOrderDetailsRequest(
    val line_items: List<LineItems> =  listOf(LineItems(
        quantity = 1,
        price = 0.0,
        title = "n",
        product_id = "",
        variant_id = null,
    )),
    val customer: CustomerDraftRequest
)

data class LineItems(
    val quantity: Int=1,
    val price: Double = 0.0,
    val title: String? = null,
    val product_id: String = "",
    val variant_id: Long? ,

    )

 */

