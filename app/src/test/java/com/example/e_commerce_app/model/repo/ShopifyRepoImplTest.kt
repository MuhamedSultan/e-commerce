package com.example.e_commerce_app.model.repo

import com.example.e_commerce_app.data.source.FakeLocalDataSource
import com.example.e_commerce_app.data.source.FakeRemoteDataSource
import com.example.e_commerce_app.db.LocalDataSource
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.network.RemoteDataSource
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test


class ShopifyRepoImplTest {
    private lateinit var repo: ShopifyRepoImpl
    private lateinit var remoteDataSourceImpl: RemoteDataSource
    private lateinit var localDataSourceImpl: LocalDataSource

    @Before
    fun setUp() {
        remoteDataSourceImpl = FakeRemoteDataSource()
        localDataSourceImpl = FakeLocalDataSource()
        repo = ShopifyRepoImpl(remoteDataSourceImpl, localDataSourceImpl)
    }

    @Test
    fun `when getAllBrands is called then it returns a non-null result`() = runTest {
        // when the call getAllBrands function
        val result = repo.getAllBrands()

        //then the result should not be null
        assertThat(result.data, `is`(notNullValue()))
    }

    @Test
    fun `when getAllBrands is called then it contains specific brand in list`() = runTest {
        // when call getAllBrands
        val result = repo.getAllBrands()

        // then the title of the second brand should be "ASICS TIGER"
        assertThat(result.data!!.smart_collections[1].title, `is`("ASICS TIGER"))
    }


    @Test
    fun `when getCategories is called then it returns a non-null result`() = runTest {
        // when the call getAllCategories function
        val result = repo.getCategories()

        //then the result should not be null
        assertThat(result.data, `is`(notNullValue()))
    }

    @Test
    fun `when getCategories is called then it contains specific category in list`() = runTest {
        // when call getAllCategories
        val result = repo.getCategories()

        // then the title of the third category should be "MEN"
        assertThat(result.data!!.custom_collections[2].title, `is`("MEN"))
    }



    @Test
    fun `when products of selected category is called then it returns a non-null result`() = runTest {
        // when the call getProductsOfSelectedCategory function
        val result = repo.getProductsOfSelectedCategory(9828914725179)

        //then the result should not be null
        assertThat(result.data, `is`(notNullValue()))
    }

    @Test
    fun `when products of selected category is called then it contains specific product in list`() = runTest {
        // when call getProductsOfSelectedCategory
        val result = repo.getProductsOfSelectedCategory(9828914725179)

        // then the title of first product of selected category should be "MEN"
        assertThat(result.data!!.products[0].title, `is`("VANS |AUTHENTIC | LO PRO | BURGANDY/WHITE"))
    }
    @Test
    fun `when getProductBrand  is called then it returns a non-null result`() = runTest {
        // when the call getProductsOfSelectedCategory function
        val result = repo.getBrandProducts("ADIDAS")

        //then the result should not be null
        assertThat(result.data, `is`(notNullValue()))
    }


    @Test
    fun `when getProductBrand is called then it contains specific product in list`() = runTest {
        // when call getProductsOfSelectedCategory
        val result = repo.getBrandProducts("ADIDAS")

        // then the title of first product of selected category should be "MEN"
        assertThat(result.data!!.products[0].title, `is`("ADIDAS | CLASSIC BACKPACK"))
    }

    @Test
    fun `when getCustomerOrders  is called then it returns a non-null result`() = runTest {
        // when the call getProductsOfSelectedCategory function
        val result = repo.getCustomerOrders(8939518099771)

        //then the result should not be null
        assertThat(result.data, `is`(notNullValue()))
    }


    @Test
    fun `when getCustomerOrders is called then it contains specific order in list`() = runTest {
        // when call getProductsOfSelectedCategory
        val result = repo.getCustomerOrders(8939518099771)

        // then the title of first product of selected category should be "MEN"
        assertThat(result.data!!.orders[0].id, `is`(6308399186235))
    }






}