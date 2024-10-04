package com.example.e_commerce_app.setting

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.e_commerce_app.R
import com.example.e_commerce_app.auth.login.view.LoginActivity
import com.example.e_commerce_app.databinding.FragmentSettingBinding
import com.example.e_commerce_app.model.user.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SettingFragment : Fragment() {
    lateinit var binding: FragmentSettingBinding

    private val firestore: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUserData()



        binding.ordersSettingLayout.setOnClickListener {

            val action = SettingFragmentDirections.actionSettingFragmentToOrdersFragment()
            findNavController().navigate(action)
        }
        binding.addressSettingLayout.setOnClickListener {
            //todo map
            val action = SettingFragmentDirections.actionSettingFragmentToMapFragment()
            findNavController().navigate(action)
        }
        binding.favoritesSettingLayout.setOnClickListener {
            //todo go to fav
            val action = SettingFragmentDirections.actionSettingFragmentToOrdersFragment()
            findNavController().navigate(action)
        }
        binding.logoutSettingLayout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

    }

    private fun logout() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()

        // Clear user data from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear() // Remove all user data
            apply()
        }

        // Navigate back to LoginActivity
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Close the current activity
    }



    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun fetchUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userData = document.toObject(UserData::class.java)
                        userData?.let {
                            binding.tvUserName.text = "Hello, ${it.userName}"
                            binding.tvUserEmail.text = it.email
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle the error
                    binding.tvUserName.text = "Error fetching data"
                    binding.tvUserEmail.text = ""
                }
        } else {
            // User is not logged in
            binding.tvUserName.text = "No user logged in"
            binding.tvUserEmail.text = ""
        }
    }
}
