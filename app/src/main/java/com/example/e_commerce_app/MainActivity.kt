package com.example.e_commerce_app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.e_commerce_app.databinding.ActivityMainBinding
import com.example.e_commerce_app.util.ConnectivityHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var connectivityHelper: ConnectivityHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connectivityHelper = ConnectivityHelper(this)
        navController = findNavController(this, R.id.navHostfragment)
        setupWithNavController(binding.bottomNavigationView, navController)


        connectivityHelper.observe(this) { isConnected ->
            if (!isConnected) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.connectionLostTv.visibility = View.VISIBLE
                binding.groupLayout.visibility = View.GONE
            } else {
                binding.lottieAnimationView.visibility = View.GONE
                binding.connectionLostTv.visibility = View.GONE
                binding.groupLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.visibility = View.VISIBLE
    }


}