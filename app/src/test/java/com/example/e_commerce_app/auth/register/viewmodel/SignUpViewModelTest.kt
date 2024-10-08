package com.example.e_commerce_app.auth.register.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.e_commerce_app.data.source.FakeRemoteDataSource
import com.example.e_commerce_app.data.source.FakeShopifyRepository
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class SignUpViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var fakeShopifyRepo: FakeShopifyRepository


    @Before
    fun setUp() {
        fakeShopifyRepo = FakeShopifyRepository()
        signUpViewModel = SignUpViewModel(fakeShopifyRepo)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun registerUser_success_userRegistered() = runBlockingTest {
        // Given user data
        val userData = UserData(
            id = 0L,
            userName = "Test User",
            email = "testuser@example.com",
            password = "password",
            phoneNumber = "1234567890"
        )

        // When registering the user
        signUpViewModel.registerUser(userData.email, userData.password, userData.userName, userData.phoneNumber)

        // Then verify success state
        val result = signUpViewModel.signUpState.value

        // Verify that the result is of type Success
        assertThat(result, `is`(instanceOf(ApiState.Success::class.java)))

        val successResult = result as? ApiState.Success<Unit>
        assertThat(successResult?.data, `is`(Unit))
    }


    // Test case for user registration failure
    @Test
    fun registerUser_failure_userRegistrationFailed() = runBlockingTest {
        // Given user data with empty fields to simulate an error
        val userData = UserData(
            id = 0L,
            userName = "Test User",
            email = "testuser@example.com",
            password = "password",
            phoneNumber = "1234567890"
        )

        // Simulate error
        fakeShopifyRepo.shouldReturnError = true

        // When attempting to register the user
        signUpViewModel.registerUser(userData.email, userData.password, userData.userName, userData.phoneNumber)

        // Then verify error state
        val result = signUpViewModel.signUpState.value

        // Verify that the result is of type Error
        assertThat(result, `is`(instanceOf(ApiState.Error::class.java)))

        // Check the contained error message
        val errorResult = result as? ApiState.Error
        assertThat(errorResult?.message, `is`("User registration failed"))
    }

}
