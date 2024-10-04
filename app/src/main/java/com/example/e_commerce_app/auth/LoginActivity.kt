package com.example.e_commerce_app.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.databinding.ActivityLoginBinding
import com.example.e_commerce_app.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var tv_signUp: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        tv_signUp = binding.tvSignUp
        tv_signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.edtSignInEmail.text.toString().trim()
            val password = binding.edtSignInPassword.text.toString().trim()

            if (validateInput(email, password)) {
                // Login user
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = firebaseAuth.currentUser?.uid
                            // Retrieve user data from Firestore
                            firestore.collection("users").document(userId!!)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        val userData = document.toObject(UserData::class.java)
                                        // Use userData as needed
                                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to retrieve user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }


    private fun validateInput(email: String, password: String): Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtSignInEmail.error = "Invalid email format"
            return false
        }
        if (password.isEmpty()) {
            binding.edtSignInPassword.error = "Password cannot be empty"
            return false
        }
        return true
    }
}


