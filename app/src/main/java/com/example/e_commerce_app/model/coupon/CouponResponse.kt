package com.example.e_commerce_app.model.coupon

data class PriceRulesResponse(
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
    val allocation_limit: Any?, // It can be null or some type, change to the appropriate type if necessary
    val once_per_customer: Boolean,
    val usage_limit: Any?, // Same as above, change to the appropriate type if needed
    val starts_at: String,
    val ends_at: String?,
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
    val prerequisite_subtotal_range: Any?, // Replace with appropriate type if needed
    val prerequisite_quantity_range: Any?, // Same as above
    val prerequisite_shipping_price_range: Any?, // Same as above
    val prerequisite_to_entitlement_quantity_ratio: PrerequisiteToEntitlementQuantityRatio,
    val prerequisite_to_entitlement_purchase: PrerequisiteToEntitlementPurchase,
    val title: String,
    val admin_graphql_api_id: String
)

data class PrerequisiteToEntitlementQuantityRatio(
    val prerequisite_quantity: Any?, // Same as above
    val entitled_quantity: Any? // Same as above
)

data class PrerequisiteToEntitlementPurchase(
    val prerequisite_amount: Any? // Same as above
)

