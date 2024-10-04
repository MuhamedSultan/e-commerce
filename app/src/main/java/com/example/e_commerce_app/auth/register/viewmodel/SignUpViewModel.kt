package com.example.e_commerce_app.auth.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val shopifyRepo: ShopifyRepo
) : ViewModel() {

    private val _signUpState = MutableStateFlow<ApiState<Unit>>(ApiState.Loading())
    val signUpState: StateFlow<ApiState<Unit>> = _signUpState

    fun registerUser(
        email: String,
        password: String,
        userName: String,
        phoneNumber: String
    ) {
        val userData = UserData(
            id = 0,
            userName = userName,
            email = email,
            password = password,
            phoneNumber = phoneNumber
        )

        viewModelScope.launch {
            _signUpState.value = ApiState.Loading()
            shopifyRepo.registerUser(userData).let { result ->
                _signUpState.value = result
            }
        }
    }


    fun validateInput(
        email: String,
        password: String,
        confirmPassword: String,
        userName: String,
        phoneNumber: String
    ): Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }
        if (password.isEmpty()) {
            return false
        }
        if (password != confirmPassword) {
            return false
        }
        if (userName.isEmpty()) {
            return false
        }
        if (phoneNumber.isEmpty() || !android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            return false
        }
        return true
    }

}
