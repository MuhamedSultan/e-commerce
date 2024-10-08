package com.example.e_commerce_app.data.source

import com.example.e_commerce_app.db.LocalDataSource
import com.example.e_commerce_app.model.product.Product

class FakeLocalDataSource : LocalDataSource {

    private val favoriteProducts = mutableListOf<Product>()

    override suspend fun addToFavorite(product: Product) {
        favoriteProducts.add(product)
    }

    override suspend fun removeFavorite(product: Product) {
        favoriteProducts.remove(product)
    }

    override suspend fun getAllFavorites(shopifyCustomerId: String): List<Product> {
        return favoriteProducts
    }
}
