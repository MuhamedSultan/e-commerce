package com.example.e_commerce_app.map

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerce_app.databinding.FragmentAddressDetailsBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.map.viewModel.AddressViewModel
import com.example.e_commerce_app.map.viewModel.AddressViewModelFactory
import com.example.e_commerce_app.model.address.AddressResponse
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import java.util.Locale

class AddressDetailsFragment : Fragment() {
    private lateinit var binding: FragmentAddressDetailsBinding
    private lateinit var viewModel: AddressViewModel
    private lateinit var customerId : String

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
        //viewModel.getAllAddresses(customerId)
        binding = FragmentAddressDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = AddressDetailsFragmentArgs.fromBundle(requireArguments())
        val latitude = args.latitude
        val longitude = args.longitude
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
                    val addressResponse = AddressResponse(
                        address1 = binding.detailedAddress.text.toString(),
                        address2 = "$city, ${address.countryCode}, ${address.adminArea}, ${address.subAdminArea}",
                        city = city,
                        country = null,
                        countryName = address.countryName,
                        countryCode = address.countryCode,
                        customerId = customerId.toLong(),
                        id = (longitude+latitude).toLong(),
                        name = "",
                        phone = binding.etPhone.text.toString(),
                        province = city,
                        zip = binding.etZip.text.toString().toLong()
                    )
                    viewModel.insertAddress(addressResponse)
                }

                Toast.makeText(requireContext(), "Location details submitted!", Toast.LENGTH_SHORT).show()
            }
        }
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
