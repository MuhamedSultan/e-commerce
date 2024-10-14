package com.example.e_commerce_app.db

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
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

        fun setProductFavoriteStatus(context: Context, productId: String, isFavorite: Boolean) {
            val prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean(KEY_FAVORITES + "_" + productId, isFavorite)
            editor.apply()
        }

        fun isProductFavorite(context: Context, productId: String): Boolean {
            val prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isFavorite =
                prefs.getBoolean(KEY_FAVORITES + "_" + productId, false)
            return isFavorite
        }


        fun saveCurrencyText(context: Context, currency: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("currency", currency)
            editor.apply()
        }
        fun getCurrencyText(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString("currency", "EUR") ?: "EUR"
        }

        fun saveCurrencyColorState(context: Context, position: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putInt("selected_currency_color_position", position)
            editor.apply()
        }

        fun getCurrencyColorState(context: Context): Int {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getInt("selected_currency_color_position", 0)
        }
         fun getPriceAndCurrency(price: Double): String {
            val sharedPrefsManager = SharedPrefsManager.getInstance()
            val currency = sharedPrefsManager.getCurrency()
            var convertedPrice = price
            if(currency == "EGP"){
                convertedPrice *= sharedPrefsManager.getCurrencyEGP()
            }else if(currency == "USD"){
                convertedPrice *= sharedPrefsManager.getCurrencyUSD()
            }
            val formattedPrice = String.format("%.2f", convertedPrice)
            return "$formattedPrice $currency"
        }
    }
}