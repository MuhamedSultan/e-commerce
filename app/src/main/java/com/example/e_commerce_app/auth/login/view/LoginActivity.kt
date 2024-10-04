package com.example.e_commerce_app.auth.login.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.model.user.repo.ShopifyRepoImpl
import com.google.firebase.auth.FirebaseAuth

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
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
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

                        saveUserData()
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    is ApiState.Error -> {
                        binding.progressBar.visibility = View.GONE

                        Toast.makeText(
                            this@LoginActivity,
                            "Error: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun saveUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            with(sharedPreferences.edit()) {
                putString("userId", it.uid)
                putString("userEmail", it.email)
                apply()
            }
        }
    }
}
