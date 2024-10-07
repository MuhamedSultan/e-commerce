package com.example.e_commerce_app.db

import android.content.Context
import android.util.Log
import com.example.e_commerce_app.model.product.Product

class LocalDataSourceImpl(private val shopifyDao: ShopifyDao) : LocalDataSource {


    override suspend fun addToFavorite(product: Product) {
        shopifyDao.addToFavorite(product)
    }

//    override suspend fun getAllFavorites(): List<Product> {
//        return shopifyDao.getAllFavorites()
//    }

    override suspend fun removeFavorite(product: Product) {
        return shopifyDao.removeFavorite(product)
    }

    override suspend fun getAllFavorites(shopifyCustomerId: String): List<Product> {
        return shopifyDao.getAllFavorites(shopifyCustomerId)
    }

    companion object {
        val PREFS_NAME: String = "ShopifyPrefs"
        val KEY_FAVORITES: String = "favorites"

        fun setMealFavoriteStatus(context: Context, mealId: String, isFavorite: Boolean) {
            val prefs =
                context.getSharedPreferences(LocalDataSourceImpl.PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean(LocalDataSourceImpl.KEY_FAVORITES + "_" + mealId, isFavorite)
            editor.apply()
        }

        fun isMealFavorite(context: Context, mealId: String): Boolean {
            val prefs =
                context.getSharedPreferences(LocalDataSourceImpl.PREFS_NAME, Context.MODE_PRIVATE)
            val isFavorite =
                prefs.getBoolean(LocalDataSourceImpl.KEY_FAVORITES + "_" + mealId, false)
            Log.d("LocalDataSource", "Meal ID: $mealId isFavorite: $isFavorite")
            return isFavorite
        }
    }


}