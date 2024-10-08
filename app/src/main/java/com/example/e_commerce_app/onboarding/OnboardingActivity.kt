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

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var btnGetStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnGetStart = binding.btnGetStart
        btnGetStart.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        animateImages()
    }

    private fun animateImages() {
        binding.imageView.visibility = View.VISIBLE
        binding.imageView.apply {
            scaleX = 0f
            scaleY = 0f
            translationY = -100f
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(500)
                .withEndAction {
                    binding.imageView2.visibility = View.VISIBLE
                    binding.imageView2.apply {
                        scaleX = 0f
                        scaleY = 0f
                        translationY = -100f
                        animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .translationY(0f)
                            .setDuration(500)
                            .setStartDelay(100)
                            .withEndAction {
                                binding.imageView3.visibility = View.VISIBLE
                                binding.imageView3.apply {
                                    scaleX = 0f
                                    scaleY = 0f
                                    translationY = -100f
                                    animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .translationY(0f)
                                        .setDuration(500)
                                        .setStartDelay(100)
                                        .withEndAction {
                                            binding.textView2.visibility = View.VISIBLE
                                            binding.textView2.alpha = 0f
                                            binding.textView2.animate()
                                                .alpha(1f)
                                                .setDuration(200)
                                                .withEndAction {
                                                    binding.textView3.visibility = View.VISIBLE
                                                    binding.textView3.alpha = 0f
                                                    binding.textView3.animate()
                                                        .alpha(1f)
                                                        .setDuration(200)
                                                        .withEndAction {
                                                            binding.btnGetStart.visibility = View.VISIBLE
                                                        }
                                                }
                                        }
                                }
                            }
                    }
                }
        }
    }
}
