package com.example.e_commerce_app.cart

import android.util.Log
import com.example.e_commerce_app.model.address.testAdd
import com.example.e_commerce_app.model.cart.AppliedDiscount
import com.example.e_commerce_app.model.cart.DraftOrder
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.LineItems
import kotlin.math.abs
import kotlin.math.floor

class DraftOrderManager private constructor(var draftOrder: DraftOrder) {

    /*private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)*/

    companion object {
        private var INSTANCE: DraftOrderManager? = null

        fun init(draftOrder: DraftOrder) {
            if (INSTANCE == null) {
                INSTANCE = DraftOrderManager(draftOrder)
            }
        }

        fun getInstance(): DraftOrderManager {
            return INSTANCE ?: throw IllegalStateException("DraftOrderManager is not initialized")
        }
    }

    fun addProductToDraftOrder(lineItem: LineItems,imageUrl : String): DraftOrder {

        // Check if a line item with the same variantId already exists in the draftOrder's lineItems list
        val existingItem = draftOrder.lineItems.find { it.variantId == lineItem.variantId }

        if (existingItem != null) {
            // If found, increase the quantity by 1
            existingItem.quantity += 1
        } else {
            // If not found, add the new line item to the list
            draftOrder.lineItems.add(lineItem)
            draftOrder.note = draftOrder.note+"|##|"+imageUrl
        }
        Log.i("TAG", "addProductToDraftOrder: ${draftOrder}")
        return draftOrder // Return the updated draftOrder
    }

    fun addAddressToDraftOrder(testAdd: testAdd): DraftOrder {
        draftOrder.billingAddress = testAdd
        draftOrder.shippingAddress = testAdd
        return draftOrder
    }

    fun addCouponToDraftOrder(appliedDiscount: AppliedDiscount): DraftOrder {
        var floorAmount = 0.0
        for(item in draftOrder.lineItems){
            floorAmount*=(item.quantity * item.price.toDouble())
        }
        appliedDiscount.amount= floor(abs(appliedDiscount.value.toDouble())*floorAmount).toString()
        draftOrder.appliedDiscount = appliedDiscount
        return draftOrder
    }


}

