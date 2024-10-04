package com.example.e_commerce_app.auth.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.util.ApiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _loginState = MutableStateFlow<ApiState<UserData>>(ApiState.Loading())
    val loginState: StateFlow<ApiState<UserData>> = _loginState

    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = ApiState.Loading()

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            fetchUserData(userId)
                        } else {
                            _loginState.value = ApiState.Error("User ID is null.")
                        }
                    } else {
                        _loginState.value =
                            ApiState.Error("Login failed: ${task.exception?.message}")
                    }
                }
        }
    }

    private fun fetchUserData(userId: String) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userData = document.toObject(UserData::class.java)
                    if (userData != null) {
                        _loginState.value = ApiState.Success(userData)
                    } else {
                        _loginState.value = ApiState.Error("Failed to convert document to UserData.")
                    }
                } else {
                    _loginState.value = ApiState.Error("Failed to retrieve user data.")
                }
            }
            .addOnFailureListener { e ->
                _loginState.value = ApiState.Error("Failed to retrieve user data: ${e.message}")
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
