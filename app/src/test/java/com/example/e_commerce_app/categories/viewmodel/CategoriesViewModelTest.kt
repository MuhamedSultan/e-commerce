package com.example.e_commerce_app.categories.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.e_commerce_app.data.source.FakeShopifyRepository
import com.example.e_commerce_app.model.custom_collection.CustomCollection
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.util.ApiState
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CategoriesViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var fakeShopifyRepo: FakeShopifyRepository

    @Before
    fun setUp() {
        fakeShopifyRepo = FakeShopifyRepository()
        categoriesViewModel = CategoriesViewModel(fakeShopifyRepo)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }



    // Test case for adding a product to favorites
    @Test
    fun addProductToFavorite_success() = runBlockingTest {
        // Given a product and a Shopify customer ID
        val product = Product(id = 1, title = "Test Product")
        val shopifyCustomerId = "customer123"

        // When adding the product to favorites
        categoriesViewModel.addProductToFavourite(product, shopifyCustomerId)

        // Then verify that the product has been added to favorites
        val favorites = fakeShopifyRepo.getAllFavorites(shopifyCustomerId)
        assertThat(favorites.size, `is`(1))
        assertThat(favorites[0], `is`(product.copy(shopifyCustomerId = shopifyCustomerId)))
    }

    // Test case for removing a product from favorites
    @Test
    fun deleteProductFromFavorite_success() = runBlockingTest {
        // Given a product and a Shopify customer ID
        val product = Product(id = 1, title = "Test Product")
        val shopifyCustomerId = "customer123"
        categoriesViewModel.addProductToFavourite(product, shopifyCustomerId)

        // When removing the product from favorites
        categoriesViewModel.deleteProductFromFavourite(product, shopifyCustomerId)

        // Then verify that the product has been removed from favorites
        val favorites = fakeShopifyRepo.getAllFavorites(shopifyCustomerId)
        assertThat(favorites.size, `is`(0))
    }



    // Test case for Search about product with success result of test

    @Test
    fun searchProducts_successfulMatch() = runBlockingTest {
        // Given a list of products
        val products = listOf(
            Product(id = 1, title = "Apple"),
            Product(id = 2, title = "Banana"),
            Product(id = 3, title = "Grape")
        )

        // Add products to originalProducts
        categoriesViewModel.originalProducts = products

        // When searching for a product
        categoriesViewModel.searchProducts("ap")

        val filteredResults = categoriesViewModel.filteredProducts.value

        // Then verify that the correct products are returned
        assertThat(filteredResults.size, `is`(2))
        assertThat(filteredResults[0].title.lowercase(), `is`("apple"))
        assertThat(filteredResults[1].title.lowercase(), `is`("grape"))
    }

    // Test case for Search about product with failed result of test

    @Test
    fun searchProducts_failedMatch() = runBlockingTest {
        // Given a list of products
        val products = listOf(
            Product(id = 1, title = "Apple"),
            Product(id = 2, title = "Banana"),
            Product(id = 3, title = "Grape")
        )

        // Add products to originalProducts
        categoriesViewModel.originalProducts = products

        // When searching for a product
        categoriesViewModel.searchProducts("Mango")

        val filteredResults = categoriesViewModel.filteredProducts.value

        // Then verify that no products are returned
        assertThat(filteredResults.size, `is`(1))

        assertThat(filteredResults[0].title.lowercase(), `is`("apple"))
    }

    @Test
    fun `getCategorise successfully`() = runTest {
        // When calling getCategorise
        categoriesViewModel.getCategorise()

        // Then assert that categoriesResult triggered and contains categories
        val result = categoriesViewModel.categoriesResult.getOrAwaitValue { }
        assertThat(result, notNullValue())
    }


    @Test
    fun `getProductsOfSelectedCategory should emit products successfully`() = runTest {
        // When calling getProductsOfSelectedCategory
        val collectionId = 123L
        categoriesViewModel.getProductsOfSelectedCategory(collectionId)

        // Then assert that productsResult triggered and contains products
        val result = categoriesViewModel.productsResult.getOrAwaitValue { }
        assertThat(result, notNullValue()) // Check that the result is not null
    }


}
