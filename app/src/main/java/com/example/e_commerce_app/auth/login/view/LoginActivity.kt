package com.example.e_commerce_app.auth.login.view

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.auth.register.view.SignUpActivity
import com.example.e_commerce_app.auth.login.viewmodel.LoginState
import com.example.e_commerce_app.auth.login.viewmodel.LoginViewModel
import com.example.e_commerce_app.auth.login.viewmodel.LoginViewModelFactory
import com.example.e_commerce_app.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var tv_signUp: TextView
//    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var firestore: FirebaseFirestore

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        firebaseAuth = FirebaseAuth.getInstance()
//        firestore = FirebaseFirestore.getInstance()

        tv_signUp = binding.tvSignUp
        tv_signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.edtSignInEmail.text.toString().trim()
            val password = binding.edtSignInPassword.text.toString().trim()

            if (loginViewModel.validateInput(email, password)) {
                loginViewModel.signInUser(email, password)
            } else {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()
    }


    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Loading -> {
                        // Show loading indicator
                    }

                    is LoginState.Success -> {
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }

                    is LoginState.Failure -> {
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
}
