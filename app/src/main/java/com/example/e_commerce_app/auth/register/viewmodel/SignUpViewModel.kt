package com.example.e_commerce_app.auth.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.user.CustomerDataRequest
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.network.Network
import com.example.e_commerce_app.util.ApiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _signUpState = MutableStateFlow<ApiState<String>>(ApiState.Loading())
    val signUpState: StateFlow<ApiState<String>> = _signUpState

    fun registerUser(email: String, password: String, userName: String, phoneNumber: String) {
        viewModelScope.launch {
            _signUpState.value = ApiState.Loading()

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        val longId = userId?.hashCode()?.toLong() ?: 0L
                        val userData = UserData(
                            id = longId,
                            userName = userName,
                            email = email,
                            password = password, // Consider secure storage of passwords
                            phoneNumber = phoneNumber
                        )

                        saveUserDataToFirestore(userId!!, userData) {
                            createShopifyCustomer(userName, email, password)
                        }
                    } else {
                        _signUpState.value = ApiState.Error("Sign-up failed: ${task.exception?.message}")
                    }
                }
        }
    }

    private fun saveUserDataToFirestore(userId: String, userData: UserData, onSuccess: () -> Unit) {
        firestore.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                _signUpState.value = ApiState.Error("Failed to save user data: ${e.message}")
            }
    }

    private fun createShopifyCustomer(userName: String, email: String, password: String) {
        val customerRequest = CustomerRequest(
            CustomerDataRequest(
                first_name = userName,
                email = email,
                verified_email = true, // Assuming email is verified by Firebase
                password = password,
                password_confirmation = password
            )
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = Network.shopifyService.createCustomer(customerRequest)
                if (response.isSuccessful) {
                    _signUpState.value = ApiState.Success("Shopify customer created!")
                } else {
                    _signUpState.value = ApiState.Error("Failed to create Shopify customer: ${response.message()}")
                }
            } catch (e: Exception) {
                _signUpState.value = ApiState.Error("Error creating Shopify customer: ${e.message}")
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
        if (userName.isEmpty()) return false
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false
        if (password.length < 6) return false
        if (password != confirmPassword) return false
        if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) return false
        return true
    }
}
