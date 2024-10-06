package com.example.e_commerce_app.model.cart

import com.google.gson.annotations.SerializedName

data class DraftOrderResponse(
    val draft_order: DraftOrderDetailsResponse
)

data class DraftOrderDetailsResponse(
    val admin_graphql_api_id: String?,
    val applied_discount: Any?,
    val billing_address: Any?,
    val completed_at: Any?,
    val created_at: String?,
    val currency: String?,
    val customer: Customer,
    val email: String?,
    val id: Long,
    val invoice_sent_at: Any?,
    val invoice_url: String?,
    val line_items: List<LineItem>,
    val name: String?,
    val note: Any?,
    val note_attributes: List<Any>?,
    val order_id: Any?,
    val payment_terms: Any?,
    val shipping_address: Any?,
    val status: String?,
    val subtotal_price: String?,
    val tags: String?,
    val tax_exempt: Boolean?,
    val tax_lines: List<TaxLineX>?,
    val taxes_included: Boolean?,
    val total_price: String?,
    val total_tax: String?,
    val updated_at: String?
)
data class Customer(
    val id: Long,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String
)
data class TaxLineX(
    val price: String?,
    val rate: Double?,
    val title: String?
)