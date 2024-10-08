package com.example.e_commerce_app.map

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.R
import com.example.e_commerce_app.brand_products.viewmodel.BrandProductViewModel
import com.example.e_commerce_app.brand_products.viewmodel.BrandProductViewModelFactory
import com.example.e_commerce_app.databinding.FragmentAddressBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.map.AddressAdapter
import com.example.e_commerce_app.map.viewModel.AddressViewModel
import com.example.e_commerce_app.map.viewModel.AddressViewModelFactory
import com.example.e_commerce_app.model.address.Address
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AddressFragment : Fragment() {
    private lateinit var binding: FragmentAddressBinding
    private lateinit var viewModel: AddressViewModel
    private lateinit var adapter: AddressAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = ShopifyDB.getInstance(requireContext()).shopifyDao()
        val localDataSource = LocalDataSourceImpl(dao)
        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource, localDataSource)
        val factory = AddressViewModelFactory(repo)
        viewModel =ViewModelProvider(this, factory)[AddressViewModel::class.java]
        var sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val customerId :String?= sharedPreferences.getString("shopifyCustomerId", null)
        if (customerId != null) {
            viewModel.getAllAddresses(customerId)
        }
        adapter = AddressAdapter()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE


        /*val addressList = listOf(
            Address(1, 30.0444, 31.2357, "Egypt", "123 Main St, Cairo", "201018870021"),
            Address(2, 51.5074, -0.1278, "UK", "456 Elm St, London", "447911123456")
        )*/

        binding.rvAddresses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddresses.adapter = adapter
        binding.btnAddLocation.setOnClickListener{
            val action = AddressFragmentDirections.actionAddressFragment2ToMapFragment()
            findNavController().navigate(action)
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addressesResult.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            showLoadingIndicator()
                        }

                        is ApiState.Success -> {
                            hideLoadingIndicator()
                            result.data?.let { addresses ->
                                adapter.submitList(addresses.addressResponses)
                            }
                        }

                        is ApiState.Error -> {
                            hideLoadingIndicator()
                            showError(result.message.toString())
                        }
                    }
                }
            }
        }

    }
    private fun showLoadingIndicator() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.rvAddresses.visibility = View.GONE
        binding.btnAddLocation.visibility = View.GONE
    }

    private fun hideLoadingIndicator() {
        binding.loadingIndicator.visibility = View.GONE
        binding.rvAddresses.visibility = View.VISIBLE
        binding.btnAddLocation.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE

    }
}
