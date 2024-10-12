package com.example.e_commerce_app.map.view

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.R
import com.example.e_commerce_app.cart.DraftOrderManager
import com.example.e_commerce_app.databinding.FragmentAddressBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.map.viewModel.AddressViewModel
import com.example.e_commerce_app.map.viewModel.AddressViewModelFactory
import com.example.e_commerce_app.model.address.AddressRequest
import com.example.e_commerce_app.model.address.AddressResponseModel
import com.example.e_commerce_app.model.address.testAdd
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
    private lateinit var page: String
    private var selectedAddress: AddressResponseModel? = null
    private var defaultAddress: AddressResponseModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = ShopifyDB.getInstance(requireContext()).shopifyDao()
        val localDataSource = LocalDataSourceImpl(dao)
        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource, localDataSource)
        val factory = AddressViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[AddressViewModel::class.java]
        val args = AddressFragmentArgs.fromBundle(requireArguments())
        page = args.page
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val customerId: String? = sharedPreferences.getString("shopifyCustomerId", null)

        if (customerId != null) {
            viewModel.getAllAddresses(customerId)
        }

        adapter = AddressAdapter(viewModel) { selected,default ->
            selectedAddress = selected
            defaultAddress = default
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        if (page=="setting"){
            binding.btnSubmit.text = "Set Default"
        }else if(page == "cart"){
            binding.btnSubmit.text = "Submit Address"
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE


        binding.rvAddresses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddresses.adapter = adapter

        binding.btnSubmit.setOnClickListener {
            if (selectedAddress != null) {
                Log.i("TAG", "onViewCreated: $selectedAddress")
                if (page == "setting"){
                    viewModel.setDefaultAddress(
                        addressId = selectedAddress!!.id,
                        addressRequest = AddressRequest(
                            testAdd(
                                address1 = selectedAddress!!.address1 ?: "Unknown Address",
                                address2 = selectedAddress!!.address2,
                                city = selectedAddress!!.city ?: "Unknown City",
                                country = "Canada",
                                phone = selectedAddress!!.phone,
                                province = "Quebec",
                                first_name = "moahmed",
                                last_name = "khedr",
                                default = true
                            )
                        )
                    )
                    defaultAddress!!.default=false
                    selectedAddress!!.default = true
                    Toast.makeText(requireContext(),"Address Updated",Toast.LENGTH_SHORT).show()
                }else if(page == "cart"){
                    Log.i("TAG", "AF: $page")
                    var shp = SharedPrefsManager.getInstance()
                    val draftOrderId = shp.getDraftedOrderId()
                    viewModel.addAddressToDraftOrder(
                        draftOrderRequest =
                            DraftOrderManager.getInstance().addAddressToDraftOrder(
                                testAdd(
                                    address1 = selectedAddress!!.address1 ?: "Unknown Address",
                                    address2 = selectedAddress!!.address2,
                                    city = selectedAddress!!.city ?: "Unknown City",
                                    country = "Canada",
                                    phone = selectedAddress!!.phone,
                                    province = "Quebec",
                                    first_name = "moahmed",
                                    last_name = "khedr",
                                    default = false
                                )
                            )
                        ,
                        draftOrderId = draftOrderId ?: 0
                    )
                    observeAddingAddress()
                }
            } else {
                // Show error message if no address is selected
                showError("Please select an address")
            }
        }

        // Handle Add Location button click
        binding.btnAddLocation.setOnClickListener {
            val action =
                AddressFragmentDirections.actionAddressFragment2ToMapFragment(
                    page
                )
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

    private fun observeAddingAddress() {
        lifecycleScope.launch {
            viewModel.draftOrderState.collect{
                    state ->
                when(state){
                    is ApiState.Success -> {
                        hideLoadingIndicator()
                        Log.i("TAG", "observeViewModel: billing:  ${state.data?.draft_order?.billing_address}\n" +
                                "shopping : ${state.data?.draft_order?.shipping_address}")
                        Toast.makeText(requireContext(),"Address added to Order Sucessfully", Toast.LENGTH_SHORT).show()
                        val action =
                            AddressFragmentDirections.actionAddressFragmentToAddingCouponFragment()
                        findNavController().navigate(action)
                    }

                    is ApiState.Error -> {
                        hideLoadingIndicator()
                        val errorMessage = state.message ?: "An unknown error occurred"
                        Log.e("TAG", "addind Address Error: ${errorMessage}")
                        showError(errorMessage)
                    }

                    is ApiState.Loading -> {
                        showLoadingIndicator()
                    }
                }
            }
        }
    }

    private fun showLoadingIndicator() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.rvAddresses.visibility = View.GONE
        binding.btnSubmit.visibility = View.GONE
        binding.btnAddLocation.visibility = View.GONE
    }

    private fun hideLoadingIndicator() {
        binding.loadingIndicator.visibility = View.GONE
        binding.rvAddresses.visibility = View.VISIBLE
        binding.btnSubmit.visibility = View.VISIBLE
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

