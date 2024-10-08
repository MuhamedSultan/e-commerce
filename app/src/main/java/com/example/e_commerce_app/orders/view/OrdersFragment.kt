package com.example.e_commerce_app.orders.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.FragmentOrdersBinding
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.model.orders.Order
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.orders.viewmodel.OrdersViewModel
import com.example.e_commerce_app.orders.viewmodel.OrdersViewModelFactory
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var ordersViewModel: OrdersViewModel
    private lateinit var  ordersAdapter :OrdersAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource)
        val factory = OrdersViewModelFactory(repo)
        ordersViewModel = ViewModelProvider(this, factory)[OrdersViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val customerName =OrdersFragmentArgs.fromBundle(requireArguments()).customerName
        binding.customerName.text=customerName
       val customerId= SharedPrefsManager.getInstance().getShopifyCustomerId()
        if (customerId != null) {
            ordersViewModel.getCustomerOrders(8936192246075)
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                ordersViewModel.ordersResult.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            showLoadingIndicator()
                        }

                        is ApiState.Success -> {
                            hideLoadingIndicator()
                            setupOrdersRecyclerview(result.data?.orders ?: emptyList())
                        }

                        is ApiState.Error -> {
                            hideLoadingIndicator()
                            showError(result.message.toString())

                        }

                    }

                }
            }
        }
        binding.viewAllTv.setOnClickListener{
            ordersAdapter.viewMore()
        }
    }

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoadingIndicator() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.groupLayout.visibility = View.GONE
    }

    private fun hideLoadingIndicator() {
        binding.loadingIndicator.visibility = View.GONE
        binding.groupLayout.visibility = View.VISIBLE
    }


    private fun setupOrdersRecyclerview(orders: List<Order>) {
         ordersAdapter = OrdersAdapter(orders){selectedOrder->
             val action=OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(selectedOrder.id)
             findNavController().navigate(action)
         }
        val manger = LinearLayoutManager(requireContext())
        binding.orderRv.apply {
            adapter = ordersAdapter
            layoutManager = manger
        }
        if (orders.isEmpty()){
            binding.groupLayout.visibility=View.INVISIBLE
        }
    }
}