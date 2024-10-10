package com.example.e_commerce_app.cart

import android.util.Log
import com.example.e_commerce_app.model.address.testAdd
import com.example.e_commerce_app.model.cart.AppliedDiscount
import com.example.e_commerce_app.model.cart.DraftOrder
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.LineItem
import com.example.e_commerce_app.model.cart.LineItems
import kotlin.math.abs
import kotlin.math.floor

class DraftOrderManager private constructor(var draftOrderRequest: DraftOrderRequest) {

    companion object {
        private var INSTANCE: DraftOrderManager? = null

        fun init(draftOrderRequest: DraftOrderRequest) {
            if (INSTANCE == null) {
                Log.i("TAG", "init: $draftOrderRequest")
                INSTANCE = DraftOrderManager(draftOrderRequest)
            }
        }

        fun getInstance(): DraftOrderManager {
            return INSTANCE ?: throw IllegalStateException("DraftOrderManager is not initialized")
        }
    }

    fun addProductToDraftOrder(lineItem: LineItems,imageUrl : String): DraftOrderRequest {
        var draftOrder = draftOrderRequest.draftOrder
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
        return draftOrderRequest // Return the updated draftOrder
    }

    fun addAddressToDraftOrder(testAdd: testAdd): DraftOrderRequest {
        var draftOrder = draftOrderRequest.draftOrder
        draftOrder.billingAddress = testAdd
        draftOrder.shippingAddress = testAdd
        return draftOrderRequest
    }

    fun addCouponToDraftOrder(appliedDiscount: AppliedDiscount): DraftOrderRequest {
        var draftOrder = draftOrderRequest.draftOrder
        draftOrder.appliedDiscount = appliedDiscount
        return draftOrderRequest
    }

    fun delete(lineItem: LineItem): DraftOrderRequest {
        var draftOrder = draftOrderRequest.draftOrder
        val result = draftOrder.note.split("|##|").filter { it.isNotEmpty() }
        var deletedIndex :Int= 0
        for ((index, item) in draftOrder.lineItems.drop(1).withIndex()) {
            if (index < result.size) {
                if (lineItem.variantId == item.variantId){
                    deletedIndex = index
                    draftOrder.lineItems.removeAt(index)
                }
            }
        }
        var tempNote:String = ""
        for ((index,item) in result.withIndex()){
            if(index!=deletedIndex){
                tempNote = tempNote +"|##|"+item
            }
        }
        draftOrder.note = tempNote

        return draftOrderRequest
    }

    fun IncreaseQuantity(lineItem: LineItem): DraftOrderRequest {
        var draftOrder = draftOrderRequest.draftOrder
        for (item in draftOrder.lineItems){
            if (lineItem.variantId == item.variantId){
                item.quantity++
                break
            }
        }
        return draftOrderRequest
    }

    fun DecreaseQuantity(lineItem: LineItem): DraftOrderRequest {
        var draftOrder = draftOrderRequest.draftOrder
        for (item in draftOrder.lineItems){
            if (lineItem.variantId == item.variantId){
                item.quantity--
                break
            }
        }
        return draftOrderRequest
    }


}

