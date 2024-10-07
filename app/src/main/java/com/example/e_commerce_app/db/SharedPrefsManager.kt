package com.example.e_commerce_app.db

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    companion object {
        private var INSTANCE: SharedPrefsManager? = null

        fun init(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = SharedPrefsManager(context.applicationContext)
            }
        }

        fun getInstance(): SharedPrefsManager {
            return INSTANCE ?: throw IllegalStateException("SharedPrefsManager is not initialized")
        }
    }

    fun getShopifyCustomerId(): String? {
        return sharedPreferences.getString("shopifyCustomerId", null)
    }


}

