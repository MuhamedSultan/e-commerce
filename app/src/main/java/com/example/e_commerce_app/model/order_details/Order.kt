package com.example.e_commerce_app.model.order_details

data class Order(
    val admin_graphql_api_id: String,
    val app_id: Long,
    val billing_address: BillingAddress,
    val browser_ip: String,
    val buyer_accepts_marketing: Boolean,
    val cancel_reason: Any,
    val cancelled_at: Any,
    val cart_token: Any,
    val checkout_id: Long,
    val checkout_token: String,
    val client_details: ClientDetails,
    val closed_at: Any,
    val company: Any,
    val confirmation_number: String,
    val confirmed: Boolean,
    val contact_email: String,
    val created_at: String,
    val currency: String,
    val current_subtotal_price: String,
    val current_subtotal_price_set: CurrentSubtotalPriceSet,
    val current_total_additional_fees_set: Any,
    val current_total_discounts: String,
    val current_total_discounts_set: CurrentTotalDiscountsSet,
    val current_total_duties_set: Any,
    val current_total_price: String,
    val current_total_price_set: CurrentTotalPriceSet,
    val current_total_tax: String,
    val current_total_tax_set: CurrentTotalTaxSet,
    val customer: Customer,
    val customer_locale: String,
    val device_id: Any,
    val discount_applications: List<Any>,
    val discount_codes: List<Any>,
    val email: String,
    val estimated_taxes: Boolean,
    val financial_status: String,
    val fulfillment_status: Any,
    val fulfillments: List<Any>,
    val id: Long,
    val landing_site: Any,
    val landing_site_ref: Any,
    val line_items: List<LineItem>,
    val location_id: Long,
    val merchant_of_record_app_id: Any,
    val name: String,
    val note: String,
    val note_attributes: List<Any>,
    val number: Int,
    val order_number: Int,
    val order_status_url: String,
    val original_total_additional_fees_set: Any,
    val original_total_duties_set: Any,
    val payment_gateway_names: List<Any>,
    val payment_terms: Any,
    val phone: Any,
    val po_number: Any,
    val presentment_currency: String,
    val processed_at: String,
    val reference: String,
    val referring_site: Any,
    val refunds: List<Any>,
    val shipping_address: ShippingAddress,
    val shipping_lines: List<Any>,
    val source_identifier: String,
    val source_name: String,
    val source_url: Any,
    val subtotal_price: String,
    val subtotal_price_set: SubtotalPriceSet,
    val tags: String,
    val tax_exempt: Boolean,
    val tax_lines: List<Any>,
    val taxes_included: Boolean,
    val test: Boolean,
    val token: String,
    val total_discounts: String,
    val total_discounts_set: TotalDiscountsSet,
    val total_line_items_price: String,
    val total_line_items_price_set: TotalLineItemsPriceSet,
    val total_outstanding: String,
    val total_price: String,
    val total_price_set: TotalPriceSet,
    val total_shipping_price_set: TotalShippingPriceSet,
    val total_tax: String,
    val total_tax_set: TotalTaxSet,
    val total_tip_received: String,
    val total_weight: Int,
    val updated_at: String,
    val user_id: Any
)