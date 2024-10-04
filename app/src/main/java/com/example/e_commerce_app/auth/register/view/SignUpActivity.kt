package com.example.e_commerce_app.auth.register.view

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.auth.register.viewmodel.SignUpViewModel
import com.example.e_commerce_app.auth.register.viewmodel.SignUpViewModelFactory
import com.example.e_commerce_app.auth.login.view.LoginActivity
import com.example.e_commerce_app.databinding.ActivitySignUpBinding
import com.example.e_commerce_app.util.ApiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    lateinit var tv_signIn: TextView

    private val signUpViewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
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

            if (signUpViewModel.validateInput(email, password, confirmPassword, userName, phoneNumber)) {
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
                        // Show loading indicator
                    }
                    is ApiState.Success -> {
                        Toast.makeText(this@SignUpActivity, state.data ?: "SignUp Success ,Back To Login", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    is ApiState.Error -> {
                        Toast.makeText(this@SignUpActivity, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
