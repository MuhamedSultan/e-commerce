package com.example.e_commerce_app.model.repo

import com.example.e_commerce_app.model.custom_collection.CustomCollectionResponse
import com.example.e_commerce_app.model.product.Product
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

    override suspend fun signInUser(email: String, password: String): ApiState<UserData> {
        return remoteDataSource.signInUser(email, password)
    }

    override suspend fun registerUser(userData: UserData): ApiState<Unit> {
        return remoteDataSource.registerUser(userData)
    }

    override suspend fun createShopifyCustomer(customerRequest: CustomerRequest): ApiState<Unit> {
        return remoteDataSource.createShopifyCustomer(customerRequest)
    }

    override suspend fun getProductById(productId: Long): ApiState<Product> {
       return remoteDataSource.getProductById(productId)
    }

    override suspend fun getCategories(): ApiState<CustomCollectionResponse> {
        return remoteDataSource.getCategories()
    }

    override suspend fun getProductsOfSelectedCategory(collectionId: Long): ApiState<ProductResponse> {
        return remoteDataSource.getProductsOfSelectedBrand(collectionId)
    }


    override suspend fun getRandomProducts(): ApiState<ProductResponse> {
       return remoteDataSource.getRandomProducts()
    }

    override suspend fun getBrandProducts(brandName: String): ApiState<ProductResponse> {
        return remoteDataSource.getBrandProducts(brandName)
    }

}