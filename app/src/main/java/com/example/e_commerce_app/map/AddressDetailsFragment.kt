package com.example.e_commerce_app.map

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.e_commerce_app.databinding.FragmentAddressDetailsBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.map.viewModel.AddressViewModel
import com.example.e_commerce_app.map.viewModel.AddressViewModelFactory
import com.example.e_commerce_app.model.address.AddressReqModel
import com.example.e_commerce_app.model.address.AddressRequest
import com.example.e_commerce_app.model.address.testAdd
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale

class AddressDetailsFragment : Fragment() {
    private lateinit var binding: FragmentAddressDetailsBinding
    private lateinit var viewModel: AddressViewModel
    private lateinit var customerId : String
    private lateinit var page : String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val dao = ShopifyDB.getInstance(requireContext()).shopifyDao()
        val localDataSource = LocalDataSourceImpl(dao)
        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource, localDataSource)
        val factory = AddressViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[AddressViewModel::class.java]
        var sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        customerId = sharedPreferences.getString("shopifyCustomerId", null)!!
        Log.i("TAG", "customerId: $customerId")
        //viewModel.getAllAddresses(customerId)
        binding = FragmentAddressDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = AddressDetailsFragmentArgs.fromBundle(requireArguments())
        val latitude = args.latitude
        val longitude = args.longitude
        page = args.page
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)

        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val city = address.locality ?: "Unknown City"
            binding.mapAddress.text = "$city \n" +
                    "${address.countryCode}, ${address.adminArea}, ${address.subAdminArea}\n" +
                    "Postal Code: ${address.postalCode}"
        }

        // Submit button click listener
        binding.btnSubmit.setOnClickListener {
            if (validateFields()) {
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: "Unknown City"
                    val userName = SharedPrefsManager.getInstance().getUserName()?:"Unknown User"
                    val result = userName.split("|##|").filter { it.isNotEmpty() }
                    var firstName = ""
                    var lastName = ""
                    if (result.size >1){
                        firstName = result[0]
                        lastName = result[1]
                    }else{
                        firstName = userName
                    }
                    val testAdd1 = testAdd(
                        address1 = binding.detailedAddress.text.toString(),
                        address2 = "${address.countryCode}, ${address.adminArea}, ${address.subAdminArea} ,$city",
                        city = city,
                        country = "Canada",
                        phone = binding.etPhone.text.toString(),
                        province = "Quebec",
                        first_name = firstName,
                        last_name = lastName
                    )
                    Log.i("TAG", "onViewCreated: $testAdd1")
                    viewModel.insertAddress(customerId,AddressRequest(testAdd1))
                    observeInsertAddress()
                }

            }
        }
    }
    private fun observeInsertAddress() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.insertAddressesResult.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            showLoadingIndicator()
                        }

                        is ApiState.Success -> {
                            hideLoadingIndicator()
                            viewModel.getAllAddresses(customerId)
                            Toast.makeText(requireContext(), "Location details submitted!", Toast.LENGTH_SHORT).show()
                            val action = AddressDetailsFragmentDirections.actionAddressDetailsFragmentToAddressFragment2(page)
                            findNavController().navigate(action)
                        }

                        is ApiState.Error -> {
                            hideLoadingIndicator()
                            Log.e("TAG", "observeInsertAddress: ", Throwable(result.message))
                            showError(result.message.toString())
                        }
                    }
                }
            }
        }
    }
    private fun showLoadingIndicator() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.GONE
        binding.mapAddress.visibility = View.GONE
        binding.cityLayout.visibility = View.GONE
        binding.zipLayout.visibility = View.GONE
        binding.phoneLayout.visibility = View.GONE
        binding.btnSubmit.visibility = View.GONE
    }

    private fun hideLoadingIndicator() {
        binding.loadingIndicator.visibility = View.GONE
        binding.tvTitle.visibility = View.VISIBLE
        binding.mapAddress.visibility = View.VISIBLE
        binding.cityLayout.visibility = View.VISIBLE
        binding.zipLayout.visibility = View.VISIBLE
        binding.phoneLayout.visibility = View.VISIBLE
        binding.btnSubmit.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    // Function to validate input fields
    private fun validateFields(): Boolean {
        var isValid = true

        // Validate detailed address
        val detailedAddress = binding.detailedAddress.text.toString().trim()
        if (detailedAddress.isEmpty()) {
            binding.cityLayout.error = "Address is required"
            isValid = false
        } else {
            binding.cityLayout.error = null
        }

        // Validate ZIP Code
        val zipCode = binding.etZip.text.toString().trim()
        if (zipCode.isEmpty()) {
            binding.zipLayout.error = "ZIP Code is required"
            isValid = false
        } else if (zipCode.length < 5) {  // Example validation: ZIP code must be at least 5 digits
            binding.zipLayout.error = "Invalid ZIP Code"
            isValid = false
        } else {
            binding.zipLayout.error = null
        }

        // Validate phone number
        val phone = binding.etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            binding.phoneLayout.error = "Phone number is required"
            isValid = false
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            binding.phoneLayout.error = "Invalid phone number"
            isValid = false
        } else {
            binding.phoneLayout.error = null
        }

        return isValid
    }
}
