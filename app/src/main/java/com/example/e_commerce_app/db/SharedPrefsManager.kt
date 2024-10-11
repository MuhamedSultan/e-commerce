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

    fun setDraftedOrderId(draftedId : Long) {
        val editor = sharedPreferences.edit()
        editor.putLong("draftId", draftedId)
        editor.apply()
    }

    fun getDraftedOrderId(): Long? {
        return sharedPreferences.getLong("draftId", 0)
    }

    fun getUserName(): String? {
        return sharedPreferences.getString("userName", "Unknown User")
    }

    fun getPaidStatus(): Boolean {
        return sharedPreferences.getBoolean("paid", false)
    }
    fun setPaidStatus(paidStatus : Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("paid", paidStatus)
        editor.apply()
    }


}

