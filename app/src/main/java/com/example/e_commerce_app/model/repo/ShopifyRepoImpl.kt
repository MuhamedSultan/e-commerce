package com.example.e_commerce_app.model.repo

import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.network.RemoteDataSource
import com.example.e_commerce_app.util.ApiState
import com.example.e_commerce_app.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ShopifyRepoImpl(private val remoteDataSource: RemoteDataSource) : ShopifyRepo {
    override suspend fun getAllBrands(): ApiState<SmartCollectionResponse> {
        return remoteDataSource.getAllBrands()
    }
    override fun getAllCartProducts():Flow<CartResponse> {
        return remoteDataSource.getAllCartProducts()
    }

    override suspend fun getDraftIds(customerId: String)=
        flow {
            val draftIDs = mutableListOf<String>()
            emit(ApiState.Loading)
            remoteDataSource.getDraftIds(customerId).addOnSuccessListener {
                if (it.exists()) {
                    draftIDs.add(it.data?.get(Constants.DRAFT_FAVORITE_ID) as String)
                    draftIDs.add(it.data?.get(Constants.DRAFT_CART_ID) as String)
                }
            }.await()
            emit(ApiState.Success(draftIDs))
        }.catch {
            emit(ApiState.Error("${it.message}"))
        }


}