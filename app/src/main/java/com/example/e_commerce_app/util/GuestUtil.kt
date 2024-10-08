package com.example.e_commerce_app.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.e_commerce_app.model.product.Product

object  GuestUtil {

    fun handleFavoriteClick(context: Context, sharedPreferences: SharedPreferences, product: Product) {
        val isGuest = sharedPreferences.getBoolean("isGuest", false)

        if (isGuest) {
            Toast.makeText(context, "Please log in to add items to favorites.", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("FavoriteUtils", "Added ${product.title} to favorites")
        }
    }
}
