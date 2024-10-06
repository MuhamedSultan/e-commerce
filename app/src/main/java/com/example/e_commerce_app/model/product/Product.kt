package com.example.e_commerce_app.model.product

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.e_commerce_app.db.ProductTypeConverters


@Entity(tableName = "fav_table")
data class Product @JvmOverloads constructor(
    val admin_graphql_api_id: String = "",
    val body_html: String = "",
    val created_at: String = "",
    val handle: String = "",

    val shopifyCustomerId: String = "",
    @PrimaryKey
    val id: Long = 0L,
    val image: Image = Image(
        admin_graphql_api_id = "",
        alt = "",
        created_at = "",
        height = 0,
        id = 0L,
        position = 0,
        product_id = 0L,
        src = "",
        updated_at = "",
        variant_ids = emptyList(),
        width = 0
    ),
    val images: List<Image> = emptyList(),
    val options: List<Option> = emptyList(),
    val product_type: String = "",
    val published_at: String = "",
    val published_scope: String = "",
    val status: String = "",
    val tags: String = "",
    val template_suffix: Any? = null,
    val title: String = "",
    val updated_at: String = "",
    val variants: List<Variant> = emptyList(),
    val vendor: String = "",
    val userId: String = "",
)
