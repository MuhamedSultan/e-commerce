package com.example.e_commerce_app.model.repo

import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.network.RemoteDataSource
import com.example.e_commerce_app.util.ApiState

class ShopifyRepoImpl(private val remoteDataSource: RemoteDataSource) : ShopifyRepo {
    override suspend fun getAllBrands(): ApiState<SmartCollectionResponse> {
        return remoteDataSource.getAllBrands()
    }

<<<<<<< HEAD:app/src/main/java/com/example/e_commerce_app/model/user/repo/ShopifyRepoImpl.kt
    //UserAuth
    override suspend fun signInUser(email: String, password: String): ApiState<UserData> {
        return remoteDataSource.signInUser(email, password)
    }

    override suspend fun registerUser(userData: UserData): ApiState<Unit> {
        return remoteDataSource.registerUser(userData)
    }

    override suspend fun createShopifyCustomer(customerRequest: CustomerRequest): ApiState<Unit> {
        return remoteDataSource.createShopifyCustomer(customerRequest)
    }


=======
    override suspend fun getRandomProducts(): ApiState<ProductResponse> {
       return remoteDataSource.getRandomProducts()
    }

>>>>>>> ed325631fc5f048c7321e067cbde92cb28ef4898:app/src/main/java/com/example/e_commerce_app/model/repo/ShopifyRepoImpl.kt
}