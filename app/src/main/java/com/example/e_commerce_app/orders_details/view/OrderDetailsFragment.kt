package com.example.e_commerce_app.orders_details.view

import android.annotation.SuppressLint
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
import com.example.e_commerce_app.databinding.FragmentOrderDetailsBinding
import com.example.e_commerce_app.model.order_details.LineItem
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.orders.view.OrdersFragmentDirections
import com.example.e_commerce_app.orders.view.OrdersFragmentDirections.ActionOrdersFragmentToOrderDetailsFragment
import com.example.e_commerce_app.orders_details.viewmodel.OrderDetailsViewModel
import com.example.e_commerce_app.orders_details.viewmodel.OrderDetailsViewModelFactory
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var orderDetailsViewModel: OrderDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource)
        val factory = OrderDetailsViewModelFactory(repo)
        orderDetailsViewModel = ViewModelProvider(this, factory)[OrderDetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val orderId = OrderDetailsFragmentArgs.fromBundle(requireArguments()).orderId
        orderDetailsViewModel.getOrderDetailsById(orderId)
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderDetailsViewModel.orderDetailsResult.collect { result ->
                    when(result){
                        is ApiState.Loading ->{
                            showLoadingIndicator()
                        }
                        is ApiState.Success->{
                            hideLoadingIndicator()
                            val order = result.data?.orders?.get(0)
                            binding.orderIdTv.text = "#${order?.order_number}"
                            binding.phone.text = order?.phone.toString()
                            binding.location.text =
                                order?.billing_address?.address1 + ", " +
                                        order?.billing_address?.city + ", " + order?.billing_address?.country
                            binding.subTotal.text = order?.subtotal_price + " " + order?.currency
                            binding.totalTax.text = order?.total_tax + " " + order?.currency
                            binding.totalCost.text = order?.total_price + " " + order?.currency
                            setupProductRecyclerView(order?.line_items?: emptyList())
                        }
                        is ApiState.Error->{
                            hideLoadingIndicator()
                            showError(result.message.toString())
                        }
                    }

                }

            }
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
    private fun setupProductRecyclerView(item:List<LineItem>){
        val orderProductsAdapter =OrderProductsAdapter(item){onProductClick->
            val action=OrderDetailsFragmentDirections.actionOrderDetailsFragmentToProductDetailsFragment(onProductClick.product_id)
            findNavController().navigate(action)
        }
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL

        binding.productItemsRv.apply {
            adapter = orderProductsAdapter
            layoutManager = manager
        }
    }
}