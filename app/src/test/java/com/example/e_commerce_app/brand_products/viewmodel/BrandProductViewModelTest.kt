package com.example.e_commerce_app.brand_products.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.e_commerce_app.data.source.FakeShopifyRepository
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollection
import com.example.e_commerce_app.util.ApiState
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.annotation.Nonnull


class BrandProductViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var brandProductViewModel: BrandProductViewModel
    private lateinit var fakeShopifyRepo: FakeShopifyRepository


    @Before
    fun setUp() {
        fakeShopifyRepo = FakeShopifyRepository()
        brandProductViewModel = BrandProductViewModel(fakeShopifyRepo)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }


    // Test case for adding a product to favorites
    @Test
    fun addProductToFavorite_success() = runBlockingTest {
        // Given a product and a Shopify customer ID
        val product = Product(id = 1, title = "Test Product")
        val shopifyCustomerId = "customer123"

        // When adding the product to favorites
        brandProductViewModel.addProductToFavourite(product, shopifyCustomerId)

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
        brandProductViewModel.addProductToFavourite(product, shopifyCustomerId)

        // When removing the product from favorites
        brandProductViewModel.deleteProductFromFavourite(product, shopifyCustomerId)

        // Then verify that the product has been removed from favorites
        val favorites = fakeShopifyRepo.getAllFavorites(shopifyCustomerId)
        assertThat(favorites.size, `is`(0))
    }

    @Test
    fun `getBrandProducts should update brandProductResult when data is available`() = runTest {
        // when calling getBrandProduct
        val brandName = "ASICS TIGER"
        brandProductViewModel.getBrandProducts(brandName)
        // then assert that brand product result
        val result = brandProductViewModel.brandProductResult.getOrAwaitValue {}
        assertThat(result, notNullValue())
    }
}