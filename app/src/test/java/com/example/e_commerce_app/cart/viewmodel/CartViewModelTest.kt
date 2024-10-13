package com.example.e_commerce_app.cart.viewmodel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.e_commerce_app.data.source.FakeShopifyRepository
import com.example.e_commerce_app.util.ApiState
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CartViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: CartViewModel
    private lateinit var fakeRepo: FakeShopifyRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        fakeRepo = FakeShopifyRepository()
        viewModel = CartViewModel(fakeRepo)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `getProductsFromDraftOrder returns success`() = runTest {
        // When calling the function
        viewModel.getProductsFromDraftOrder(123L)

        // Then observe the StateFlow for success
        val result = viewModel.draftOrderState.getOrAwaitValue { }
        MatcherAssert.assertThat(result, notNullValue())
    }

    @Test
    fun `getAllCoupons returns success`() = runTest {
        // When calling the function
        viewModel.getAllCoupons()

        // Then observe the StateFlow for success
        val result = viewModel.allCouponsResult.getOrAwaitValue {  }
        MatcherAssert.assertThat(result, notNullValue())
        /*assertTrue(result is ApiState.Success)*/
        assertNotNull((result as ApiState.Success).data?.price_rules ?: null)
    }
}
