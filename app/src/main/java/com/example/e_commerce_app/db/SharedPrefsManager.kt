package com.example.e_commerce_app.db

import android.content.Context
import android.content.SharedPreferences
import com.example.e_commerce_app.db.LocalDataSourceImpl.Companion.PREFS_NAME

class SharedPrefsManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    private val shopifySharedPreferences: SharedPreferences =
        context.getSharedPreferences("ShopifyPrefs", Context.MODE_PRIVATE)

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

    fun setDraftedOrderId(draftedId: Long) {
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

    fun setPaidStatus(paidStatus: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("paid", paidStatus)
        editor.apply()
    }

    fun getCurrencyLastDate(): String? {
        return sharedPreferences.getString("currency_date", "")
    }

    fun setCurrencyLastDate(currencyDate: String) {
        val editor = sharedPreferences.edit()
        editor.putString("currency_date", currencyDate)
        editor.apply()
    }

    fun setCurrencyEGP(egp: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat("EGP", egp)
        editor.apply()
    }

    fun getCurrencyEGP(): Float {
        return sharedPreferences.getFloat("EGP", 1f)
    }

    fun setCurrencyEUR(eur: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat("EUR", eur)
        editor.apply()
    }

    fun getCurrencyEUR(): Float {
        return sharedPreferences.getFloat("EUR", 1f)
    }

    fun setCurrencyUSD(usd: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat("USD", usd)
        editor.apply()
    }

    fun getCurrencyUSD(): Float {
        return sharedPreferences.getFloat("USD", 1f)
    }

    fun getCurrency(): String {
        return shopifySharedPreferences.getString("currency", "EUR") ?: "EUR"
    }

}

