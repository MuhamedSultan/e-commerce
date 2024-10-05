package com.example.e_commerce_app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.e_commerce_app.model.product.Product

@Dao
interface ShopifyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToFavorite(product: Product)

}