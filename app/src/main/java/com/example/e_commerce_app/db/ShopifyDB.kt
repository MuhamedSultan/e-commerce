package com.example.e_commerce_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.e_commerce_app.model.product.Product

@Database([Product::class], version = 7, exportSchema = false)
@TypeConverters(ProductTypeConverters::class)
abstract class ShopifyDB : RoomDatabase() {

    abstract fun shopifyDao(): ShopifyDao

    companion object {
        @Volatile
        private var INSTANCE: ShopifyDB? = null

        fun getInstance(context: Context): ShopifyDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, ShopifyDB::class.java, "shopify_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }

        }
    }
}