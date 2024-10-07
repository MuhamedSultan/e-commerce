package com.example.e_commerce_app.auth.register.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce_app.auth.register.viewmodel.SignUpViewModel
import com.example.e_commerce_app.auth.register.viewmodel.SignUpViewModelFactory
import com.example.e_commerce_app.auth.login.view.LoginActivity
import com.example.e_commerce_app.databinding.ActivitySignUpBinding
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var tv_signIn: TextView

    // Initialize the RemoteDataSource
    private val remoteDataSource = RemoteDataSourceImpl()
    private val shopifyRepo = ShopifyRepoImpl(remoteDataSource)

    private val signUpViewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory(shopifyRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tv_signIn = binding.tvSignIn
        tv_signIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.edtSignInEmail.text.toString().trim()
            val password = binding.edtSignInPassword.text.toString().trim()
            val confirmPassword = binding.edtSignInConfirmPassword.text.toString().trim()
            val userName = binding.edtSignInUserName.text.toString().trim()
            val phoneNumber = binding.edtSignInPhoneNumber.text.toString().trim()

            if (signUpViewModel.validateInput(
                    email,
                    password,
                    confirmPassword,
                    userName,
                    phoneNumber
                )
            ) {
                binding.progressBar3.visibility = View.VISIBLE

                signUpViewModel.registerUser(email, password, userName, phoneNumber)
            } else {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            signUpViewModel.signUpState.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                    }

                    is ApiState.Success -> {
                        binding.progressBar3.visibility = View.GONE
                        signUpViewModel.sendVerificationEmail() // Send verification email

                        Toast.makeText(
                            this@SignUpActivity,
                            "SignUp Success! Please verify your email.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    is ApiState.Error -> {
                        binding.progressBar3.visibility = View.GONE
                        Toast.makeText(
                            this@SignUpActivity,
                            "Error: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
