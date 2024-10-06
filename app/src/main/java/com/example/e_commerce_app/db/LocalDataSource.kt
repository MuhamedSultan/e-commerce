package com.example.e_commerce_app.db

import com.example.e_commerce_app.model.product.Product

interface LocalDataSource {

   suspend fun addToFavorite(product: Product)
//   suspend fun getAllFavorites(): List<Product>
   suspend fun removeFavorite(product: Product)


   suspend fun getAllFavorites(userId: String): List<Product>
}