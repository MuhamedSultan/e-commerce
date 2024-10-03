package com.example.e_commerce_app.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.e_commerce_app.R
import com.example.e_commerce_app.auth.LoginActivity
import com.example.e_commerce_app.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    lateinit var binding: ActivityOnboardingBinding
    lateinit var btn_getStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        btn_getStart = binding.btnGetStart
        btn_getStart.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}