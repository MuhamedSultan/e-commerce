package com.example.e_commerce_app.model.cart

data class PriceRuleResponse(
    val price_rules: List<PriceRule>
)

data class PriceRule(
    val id: Long,
    val value_type: String,
    val value: String,
    val customer_selection: String,
    val target_type: String,
    val target_selection: String,
    val allocation_method: String,
    val allocation_limit: Any?, // Nullable since it's sometimes null
    val once_per_customer: Boolean,
    val usage_limit: Any?, // Nullable
    val starts_at: String,
    val ends_at: Any?, // Nullable
    val created_at: String,
    val updated_at: String,
    val entitled_product_ids: List<Long>,
    val entitled_variant_ids: List<Long>,
    val entitled_collection_ids: List<Long>,
    val entitled_country_ids: List<Long>,
    val prerequisite_product_ids: List<Long>,
    val prerequisite_variant_ids: List<Long>,
    val prerequisite_collection_ids: List<Long>,
    val customer_segment_prerequisite_ids: List<Long>,
    val prerequisite_customer_ids: List<Long>,
    val prerequisite_subtotal_range: Any?, // Nullable
    val prerequisite_quantity_range: Any?, // Nullable
    val prerequisite_shipping_price_range: Any?, // Nullable
    val prerequisite_to_entitlement_quantity_ratio: EntitlementQuantityRatio?,
    val prerequisite_to_entitlement_purchase: EntitlementPurchase?,
    val title: String,
    val admin_graphql_api_id: String
)

data class EntitlementQuantityRatio(
    val prerequisite_quantity: Any?, // Nullable
    val entitled_quantity: Any? // Nullable
)

data class EntitlementPurchase(
    val prerequisite_amount: Any? // Nullable
)
