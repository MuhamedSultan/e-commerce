package com.example.e_commerce_app.db

import com.example.e_commerce_app.model.product.Product

class LocalDataSourceImpl(private val shopifyDao: ShopifyDao) : LocalDataSource {
    override suspend fun addToFavorite(product: Product) {
        shopifyDao.addToFavorite(product)
    }
}