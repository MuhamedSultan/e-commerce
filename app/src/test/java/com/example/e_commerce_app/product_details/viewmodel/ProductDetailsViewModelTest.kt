package com.example.e_commerce_app.product_details.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.e_commerce_app.data.source.FakeShopifyRepository
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.util.ApiState
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
class ProductDetailsViewModelTest{

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var productDetailsViewModel: ProductDetailsViewModel
    private lateinit var fakeShopifyRepo: FakeShopifyRepository



    @Before
    fun setUp() {
        fakeShopifyRepo = FakeShopifyRepository()
        productDetailsViewModel = ProductDetailsViewModel(fakeShopifyRepo)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }


    // Test case for fetching product details
    @Test
    fun fetchProductDetails_success_retrievesProduct() = runBlockingTest {
        // Given a product ID
        val productId = 1L

        // When fetching product details
        productDetailsViewModel.fetchProductDetails(productId)

        // Then verify the product is retrieved successfully
        val result = productDetailsViewModel.productState.value
        assertThat(result is ApiState.Success, `is`(true))
        assertThat((result as ApiState.Success).data?.id, `is`(productId))
    }


    // Test case for adding a product to favorites
    @Test
    fun addToFavorite_success_addsProductToFavorites() = runBlockingTest {
        // Given a product to add to favorites
        val product = Product(id = 1, title = "Test Product 1")
        val shopifyCustomerId = "customer123"

        // When adding to favorites
        productDetailsViewModel.addToFavorite(product, shopifyCustomerId)

        // Then verify the product is added to favorites
        val favorites = fakeShopifyRepo.getAllFavorites(shopifyCustomerId)
        assertThat(favorites.size, `is`(1))
        assertThat(favorites[0].id, `is`(product.id))
    }


    // Test case for removing a product from favorites
    @Test
    fun removeFavorite_success_removesProductFromFavorites() = runBlockingTest {
        // Given a product added to favorites
        val product = Product(id = 1, title = "Test Product 1")
        val shopifyCustomerId = "customer123"
        productDetailsViewModel.addToFavorite(product, shopifyCustomerId)

        // When removing from favorites
        productDetailsViewModel.removeFavorite(product, shopifyCustomerId)

        // Then verify the product is removed from favorites
        val favorites = fakeShopifyRepo.getAllFavorites(shopifyCustomerId)
        assertThat(favorites.size, `is`(0))
    }

}