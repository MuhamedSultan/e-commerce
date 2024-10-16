package com.example.e_commerce_app.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.R
import com.example.e_commerce_app.auth.login.view.LoginActivity
import com.example.e_commerce_app.databinding.FragmentSettingBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.model.user.UserData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import java.io.IOException

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    private val firestore: FirebaseFirestore = Firebase.firestore
    private lateinit var storageReference: StorageReference
    private var customerId: String? = null


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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customerId = SharedPrefsManager.getInstance().getShopifyCustomerId()
        if (customerId != null) {
            binding.ordersSettingLayout.visibility = View.VISIBLE
            binding.favoritesSettingLayout.visibility = View.VISIBLE
            binding.addressSettingLayout.visibility = View.VISIBLE
            binding.currencySettingLayout.visibility = View.VISIBLE
            binding.ivProfilePicture.visibility = View.VISIBLE
            binding.view.visibility = View.VISIBLE
            binding.view2.visibility = View.VISIBLE
            binding.view3.visibility = View.VISIBLE
            binding.textViewLogout.text = "Logout"
            binding.textViewClickLogout.text = "click her to logout"
        } else {
            binding.ordersSettingLayout.visibility = View.GONE
            binding.favoritesSettingLayout.visibility = View.GONE
            binding.addressSettingLayout.visibility = View.GONE
            binding.currencySettingLayout.visibility = View.VISIBLE
            binding.ivProfilePicture.visibility = View.INVISIBLE
            binding.view.visibility = View.GONE
            binding.view2.visibility = View.GONE
            binding.view3.visibility = View.GONE
            binding.textViewLogout.text = "Login"
            binding.textViewClickLogout.text = "click her to login"
        }
        setupCurrencyRecyclerview()
        storageReference = FirebaseStorage.getInstance().reference

        fetchUserData()

        binding.ivProfilePicture.setOnClickListener {
            openImagePicker()
        }

        binding.ordersSettingLayout.setOnClickListener {
            val action =
                SettingFragmentDirections.actionSettingFragmentToOrdersFragment(binding.tvUserName.text.toString())
            findNavController().navigate(action)
        }
        binding.addressSettingLayout.setOnClickListener {
            val action =
                SettingFragmentDirections.actionSettingFragmentToAddressFragment2("setting")
            findNavController().navigate(action)
        }
        binding.favoritesSettingLayout.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToFavoriteFragment()
            findNavController().navigate(action)
        }
        binding.logoutSettingLayout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val imageUri: Uri? = result.data?.data
                Log.d("ImageURI", imageUri.toString())
                imageUri?.let {
                    uploadImageToFirebase(it)
                }
            }
        }


    private fun uploadImageToFirebase(imageUri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            // Log the selected image URI
            Log.d("Image URI", "Uploading image to path: test_images/$uid.jpg")

            val filePath = storageReference.child("test_images/$uid.jpg")

            filePath.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    Log.d("Upload", "Upload successful")
                    filePath.downloadUrl.addOnSuccessListener { uri ->
                        updateProfileImageInFirestore(uri.toString())
                    }.addOnFailureListener { e ->
                        Log.e("Download URL", "Failed to get download URL", e)
                        handleUploadError(e)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Upload", "Upload failed", e)
                    handleUploadError(e)
                }
        } ?: run {
            Log.e("Upload", "User ID is null")
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }


    private fun handleUploadError(e: Exception) {
        Log.e("Upload Error", e.localizedMessage ?: "Unknown error occurred")

        when (e) {
            is StorageException -> {
                when (e.errorCode) {
                    StorageException.ERROR_OBJECT_NOT_FOUND -> {
                        Log.e("Upload Error", "The object does not exist at the location.")
                        Toast.makeText(
                            requireContext(),
                            "File not found. Please upload a new image.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> {
                        Log.e("Upload Error", "Upload session terminated, retry limit exceeded.")
                        Toast.makeText(
                            requireContext(),
                            "Upload failed, please try again later.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                        Log.e("Upload Error", "Other StorageException occurred: ${e.message}")
                        Toast.makeText(
                            requireContext(),
                            "Upload failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            is IOException -> {
                Log.e("Upload Error", "IO Exception occurred: ${e.message}")
                Toast.makeText(
                    requireContext(),
                    "IO error occurred during upload: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {
                Log.e("Upload Error", "An unexpected error occurred: ${e.message}")
                Toast.makeText(
                    requireContext(),
                    "An unexpected error occurred: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun updateProfileImageInFirestore(imageUrl: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener {
                    Log.d("Firestore", "Profile image URL updated successfully")
                    // Load the image using Glide
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .into(binding.ivProfilePicture)
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error updating profile image", e)
                }
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        val sharedPreferences =
            requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val favouriteSharedPrefs =
            requireActivity().getSharedPreferences("ShopifyPrefs", Context.MODE_PRIVATE)
        favouriteSharedPrefs.edit().clear().apply()
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()

    }

    private fun showLogoutConfirmationDialog() {
        if (customerId != null) {
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
        } else {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }
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
                            // Load the profile image if it exists
                            if (it.profileImageUrl.isNotEmpty()) {
                                Glide.with(requireContext())
                                    .load(it.profileImageUrl)
                                    .into(binding.ivProfilePicture)
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    binding.tvUserName.text = "Error fetching data"
                    binding.tvUserEmail.text = ""
                }
        } else {
            binding.tvUserName.text = "No user logged in"
            binding.tvUserEmail.text = ""
        }
    }

    private fun setupCurrencyRecyclerview() {
        val currencyList = listOf("EUR", "USD", "EGP")
        val savedPosition = LocalDataSourceImpl.getCurrencyColorState(requireContext())
        val currencyAdapter = CurrencyAdapter(currencyList, { onCurrencyClick ->
            LocalDataSourceImpl.saveCurrencyText(requireContext(), onCurrencyClick)
            LocalDataSourceImpl.saveCurrencyColorState(
                requireContext(),
                currencyList.indexOf(onCurrencyClick)
            )

        }, savedPosition, requireContext())
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.currencyRv.apply {
            adapter = currencyAdapter
            layoutManager = manager
        }
    }
}
