package com.example.e_commerce_app.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.e_commerce_app.R
import com.example.e_commerce_app.auth.login.view.LoginActivity
import com.example.e_commerce_app.databinding.ActivityOnboardingBinding
import com.example.e_commerce_app.db.SharedPrefsManager

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var btnGetStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SharedPrefsManager.init(this)
        btnGetStart = binding.btnGetStart
        btnGetStart.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

}
