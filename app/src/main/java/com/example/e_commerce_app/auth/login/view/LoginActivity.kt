package com.example.e_commerce_app.auth.login.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.auth.register.view.SignUpActivity
import com.example.e_commerce_app.util.ApiState
import com.example.e_commerce_app.auth.login.viewmodel.LoginViewModel
import com.example.e_commerce_app.auth.login.viewmodel.LoginViewModelFactory
import com.example.e_commerce_app.databinding.ActivityLoginBinding
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.network.RemoteDataSourceImpl

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var tv_signUp: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private val remoteDataSource by lazy { RemoteDataSourceImpl() }
    private val shopifyRepo by lazy { ShopifyRepoImpl(remoteDataSource) }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(shopifyRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        checkUserLoggedIn()

        tv_signUp = binding.tvSignUp
        tv_signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.edtSignInEmail.text.toString().trim()
            val password = binding.edtSignInPassword.text.toString().trim()

            if (loginViewModel.validateInput(email, password)) {
                binding.progressBar.visibility = View.VISIBLE
                loginViewModel.signInUser(email, password)
            } else {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()
    }

    private fun checkUserLoggedIn() {
        val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
        Log.d("LoginActivity", "Retrieved ShopifyCustomerId from SharedPreferences: $shopifyCustomerId")
        if (shopifyCustomerId != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                    }

                    is ApiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        state.data?.let { userData ->
                            if (userData.shopifyCustomerId != null) {
                                saveUserData(userData)
                                Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Shopify Customer ID is missing!", Toast.LENGTH_SHORT).show()
                            }
                        } ?: run {
                            Toast.makeText(this@LoginActivity, "User data is null", Toast.LENGTH_SHORT).show()
                        }
                    }

                    is ApiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveUserData(userData: UserData) {

        Log.d("LoginActivity", "Saving Shopify Customer ID: ${userData.shopifyCustomerId}")

        with(sharedPreferences.edit()) {
            putString("userId", userData.id.toString())
            putString("userEmail", userData.email)
            putString("shopifyCustomerId", userData.shopifyCustomerId)
            apply()
        }
        // Log the saved value for verification
        val savedShopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
        Log.d("LoginActivity", "Saved ShopifyCustomerId in SharedPreferences: $savedShopifyCustomerId")

    }
}
