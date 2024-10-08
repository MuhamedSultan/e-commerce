package com.example.e_commerce_app.auth.login.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.e_commerce_app.data.source.FakeShopifyRepository
import com.example.e_commerce_app.model.user.UserData
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

class LoginViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var fakeShopifyRepo: FakeShopifyRepository


    @Before
    fun setUp() {
        fakeShopifyRepo = FakeShopifyRepository()
        loginViewModel = LoginViewModel(fakeShopifyRepo)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }


    // Test case for successful user sign-in
    @Test
    fun signInUser_success_retrievesUserData() = runBlockingTest {
        // Given valid credentials
        val email = "test@example.com"
        val password = "password"
        val expectedUserData = UserData(email = email)
        fakeShopifyRepo.setSignInUserResult(ApiState.Success(expectedUserData))

        // When signing in
        loginViewModel.signInUser(email, password)

        // Then verify the user data is retrieved successfully
        val result = loginViewModel.loginState.value
        assertThat(result is ApiState.Success, `is`(true))
        assertThat((result as ApiState.Success).data?.email, `is`(email))
    }

    // Test case for sign-in failure
    @Test
    fun signInUser_failure_returnsError() = runBlockingTest {
        // Given invalid credentials
        val email = "invalid@example.com"
        val password = "wrongpassword"
        fakeShopifyRepo.setSignInUserResult(ApiState.Error("Invalid credentials"))

        // When signing in
        loginViewModel.signInUser(email, password)

        // Then verify the error is returned
        val result = loginViewModel.loginState.value
        assertThat(result is ApiState.Error, `is`(true))
        assertThat((result as ApiState.Error).message, `is`("Invalid credentials"))
    }


    // Test case for validating input with valid credentials
    @Test
    fun validateInput_validInput_returnsTrue() {
        // Given valid email and password
        val email = "test@example.com"
        val password = "password"

        // When validating input
        val isValid = loginViewModel.validateInput(email, password)

        // Then verify the input is valid
        assertThat(isValid, `is`(true))
    }


    // Test case for validating input with invalid email
    @Test
    fun validateInput_invalidEmail_returnsFalse() {
        // Given an invalid email and valid password
        val email = "invalidEmail"
        val password = "password"

        // When validating input
        val isValid = loginViewModel.validateInput(email, password)

        // Then verify the input is invalid
        assertThat(isValid, `is`(false))
    }

    // Test case for validating input with empty password
    @Test
    fun validateInput_emptyPassword_returnsFalse() {
        // Given valid email and empty password
        val email = "test@example.com"
        val password = ""

        // When validating input
        val isValid = loginViewModel.validateInput(email, password)

        // Then verify the input is invalid
        assertThat(isValid, `is`(false))
    }

}