package com.example.e_commerce_app.favorite.viewmodel

import FavoriteViewModel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.e_commerce_app.data.source.FakeShopifyRepository
import com.example.e_commerce_app.model.product.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class FavoriteViewModelTest{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var fakeShopifyRepo: FakeShopifyRepository


    @Before
    fun setUp() {
        fakeShopifyRepo = FakeShopifyRepository()
        favoriteViewModel = FavoriteViewModel(fakeShopifyRepo)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }


    // Test case for retrieving favorite products
    @Test
    fun getAllFavorites_success_retrievesFavorites() = runBlockingTest {
        // Given a Shopify customer ID and a list of favorite products
        val shopifyCustomerId = "customer123"
        val favoriteProducts = listOf(
            Product(id = 1, title = "Product 1"),
            Product(id = 2, title = "Product 2")
        )
        favoriteProducts.forEach { fakeShopifyRepo.addToFavorite(it) }

        // When retrieving favorites
        favoriteViewModel.getAllFavorites(shopifyCustomerId)

        // Then verify the favorites are retrieved
        val result = favoriteViewModel.favorites.value
        assertThat(result.size, `is`(2))
        assertThat(result, `is`(favoriteProducts))
    }


    // Test case for removing a favorite product
    @Test
    fun removeFavorite_success_removesFavorite() = runBlockingTest {
        // Given a Shopify customer ID and a list of favorite products
        val shopifyCustomerId = "customer123"
        val productToRemove = Product(id = 1, title = "Product 1")
        fakeShopifyRepo.addToFavorite(productToRemove)
        fakeShopifyRepo.addToFavorite(Product(id = 2, title = "Product 2"))

        // When removing a favorite product
        favoriteViewModel.removeFavorite(productToRemove, shopifyCustomerId)

        // Then verify the favorite product is removed
        val result = favoriteViewModel.favorites.value
        assertThat(result.size, `is`(1))
        assertThat(result, `is`(listOf(Product(id = 2, title = "Product 2"))))
    }

}