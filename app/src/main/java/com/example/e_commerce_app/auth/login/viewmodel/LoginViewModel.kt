package com.example.e_commerce_app.auth.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val shopifyRepo: ShopifyRepo
) : ViewModel() {

    private val _loginState = MutableStateFlow<ApiState<UserData>>(ApiState.Loading())
    val loginState: StateFlow<ApiState<UserData>> = _loginState

    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = ApiState.Loading()
            shopifyRepo.signInUser(email, password).let { result ->
                _loginState.value = result
            }
        }
    }


    fun validateInput(email: String, password: String): Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }
        if (password.isEmpty()) {
            return false
        }
        return true
    }
}
