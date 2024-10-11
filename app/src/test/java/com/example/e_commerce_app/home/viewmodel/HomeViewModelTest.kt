package com.example.e_commerce_app.home.viewmodel

import com.example.e_commerce_app.data.source.FakeShopifyRepository
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.util.ApiState
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fakeShopifyRepo: FakeShopifyRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Given a fake repository and initialized ViewModel
        fakeShopifyRepo = FakeShopifyRepository()
        homeViewModel = HomeViewModel(fakeShopifyRepo)
    }

    @Test
    fun `getAllBrands success`() = runTest {
        // When fetching all brands
        homeViewModel.getAllBrands()

        // Then should emit a non-null value
        val result = homeViewModel.brandsResult.getOrAwaitValue { }
        assertThat(result, notNullValue())
    }

    @Test
    fun `getAllBrands error when no brands data found`() = runTest {
        // When fetching brands but no data is found
        homeViewModel.getAllBrands()

        // Then an error state should be emitted
        val result = homeViewModel.brandsResult.getOrAwaitValue { }
        assertThat(result, instanceOf(ApiState.Error::class.java))
        assertThat((result as ApiState.Error).message, equalTo("No brands data found"))
    }

    @Test
    fun `getRandomProducts success`() = runTest {
        // When fetching random products
        homeViewModel.getRandomProducts()

        // Then the should emit a non-null value
        val result = homeViewModel.randProductsResult.getOrAwaitValue { }
        assertThat(result, notNullValue())
    }

    @Test
    fun `getRandomProducts error when no products data found`() = runTest {
        // When fetching random products but no data is found
        homeViewModel.getRandomProducts()

        // Then an error state should be emitted
        val result = homeViewModel.randProductsResult.getOrAwaitValue { }
        assertThat(result, instanceOf(ApiState.Error::class.java))
        assertThat((result as ApiState.Error).message, equalTo("No product data found"))
    }

    @Test
    fun `addProductToFavourite success`() = runTest {
        // Given a product and a Shopify customer ID
        val product = Product(id = 1, title = "Test Product")
        val shopifyCustomerId = "customer123"

        // When adding the product to favourites
        homeViewModel.addProductToFavourite(product, shopifyCustomerId)
        delay(5000)
        // Then the product should be successfully added to favourites
        val favorites = fakeShopifyRepo.getAllFavorites(shopifyCustomerId)
        assertThat(favorites.size, `is`(1))
        assertThat(favorites[0].title, `is`("Test Product"))
    }

    @Test
    fun `deleteProductFromFavorite success`() = runTest {
        // Given a product and a Shopify customer ID
        val product = Product(id = 1, title = "Test Product")
        val shopifyCustomerId = "customer123"
        homeViewModel.addProductToFavourite(product, shopifyCustomerId)

        // When removing the product from favourites
        homeViewModel.deleteProductFromFavourite(product, shopifyCustomerId)

        // Then the product should be removed from favourites
        val favorites = fakeShopifyRepo.getAllFavorites(shopifyCustomerId)
        assertThat(favorites.size, `is`(0))
    }

    @Test
    fun `fetchCurrencyRates success`() = runTest {
        // When fetching currency rates
        homeViewModel.fetchCurrencyRates()

        // Then should emit non-null currency rates
        val rates = homeViewModel.currencyRates.getOrAwaitValue { }
        assertThat(rates, notNullValue())
    }
}
