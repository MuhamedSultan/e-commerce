package com.example.e_commerce_app.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.e_commerce_app.databinding.ActivitySignUpBinding
import com.example.e_commerce_app.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    lateinit var tv_signIn: TextView
    private lateinit var firestore: FirebaseFirestore


    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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

            if (validateInput(email, password, confirmPassword, userName, phoneNumber)) {
                // Register user
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = firebaseAuth.currentUser?.uid
                            // Generate a unique Long ID (you can implement your own logic)
                            val longId = userId?.hashCode()?.toLong() ?: 0L // Ensure uniqueness

                            // Create UserData object
                            val userData = UserData(
                                id = longId, // Use generated Long ID
                                userName = userName,
                                email = email,
                                password = password, // Consider not storing the password
                                phoneNumber = phoneNumber
                            )

                            // Save user data to Firestore
                            firestore.collection("users").document(userId!!)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                                    // Navigate to MainActivity
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Sign-up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }



    private fun validateInput(email: String, password: String, confirmPassword: String, userName: String, phoneNumber: String): Boolean {
        if (userName.isEmpty()) {
            binding.edtSignInUserName.error = "Username is required"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtSignInEmail.error = "Invalid email format"
            return false
        }
        if (password.length < 6) {
            binding.edtSignInPassword.error = "Password must be at least 6 characters"
            return false
        }
        if (password != confirmPassword) {
            binding.edtSignInConfirmPassword.error = "Passwords do not match"
            return false
        }
        if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            binding.edtSignInPhoneNumber.error = "Invalid phone number"
            return false
        }
        return true
    }
}
