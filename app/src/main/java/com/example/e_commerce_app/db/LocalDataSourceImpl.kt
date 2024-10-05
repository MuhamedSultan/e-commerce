package com.example.e_commerce_app.db

import com.example.e_commerce_app.model.product.Product

class LocalDataSourceImpl(private val shopifyDao: ShopifyDao) : LocalDataSource {
    override suspend fun addToFavorite(product: Product) {
        shopifyDao.addToFavorite(product)
    }

    override suspend fun getAllFavorites(): List<Product> {
        return shopifyDao.getAllFavorites()
    }

    override suspend fun removeFavorite(product: Product) {
        return shopifyDao.removeFavorite(product)
    }
}