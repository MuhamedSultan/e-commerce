package com.example.e_commerce_app.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.e_commerce_app.model.product.Product

@Dao
interface ShopifyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToFavorite(product: Product)


//    @Query("SELECT * FROM fav_table")
//    suspend fun getAllFavorites(): List<Product>

    @Delete
    suspend fun removeFavorite(product: Product)


    @Query("SELECT * FROM fav_table WHERE userId = :userId")
    suspend fun getAllFavorites(userId: String): List<Product>


}