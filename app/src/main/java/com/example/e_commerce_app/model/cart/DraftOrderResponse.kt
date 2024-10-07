package com.example.e_commerce_app.model.cart

import com.example.e_commerce_app.model.address.AddressResponseModel
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
    val accepts_marketing: Boolean?,
    val accepts_marketing_updated_at: String?,
    val admin_graphql_api_id: String?,
    val created_at: String?,
    val currency: String?,
    val default_address: AddressResponseModel?,
    val email: String?,
    val first_name: String?,
    val id: Long,
    val last_name: String?,
    val last_order_id: Any?,
    val last_order_name: Any?,
    val marketing_opt_in_level: Any?,
    val multipass_identifier: Any?,
    val note: Any?,
    val orders_count: Int?,
    val phone: String?,
    val state: String?,
    val tags: String?,
    val tax_exempt: Boolean?,
    val tax_exemptions: List<Any?>?,
    val total_spent: String?,
    val updated_at: String?,
    val verified_email: Boolean?
)
data class TaxLineX(
    val price: String?,
    val rate: Double?,
    val title: String?
)