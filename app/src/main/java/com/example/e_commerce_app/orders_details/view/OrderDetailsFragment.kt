package com.example.e_commerce_app.orders_details.view

import android.annotation.SuppressLint
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
import com.example.e_commerce_app.databinding.FragmentOrderDetailsBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.currencyResponse.Rates
import com.example.e_commerce_app.model.order_details.LineItem
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.orders_details.viewmodel.OrderDetailsViewModel
import com.example.e_commerce_app.orders_details.viewmodel.OrderDetailsViewModelFactory
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var orderDetailsViewModel: OrderDetailsViewModel
    private lateinit var selectedCurrency: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource)
        val factory = OrderDetailsViewModelFactory(repo)
        orderDetailsViewModel = ViewModelProvider(this, factory)[OrderDetailsViewModel::class.java]
        selectedCurrency = LocalDataSourceImpl.getCurrencyText(requireContext())

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
        orderDetailsViewModel.fetchCurrencyRates()
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderDetailsViewModel.orderDetailsResult.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            showLoadingIndicator()
                        }

                        is ApiState.Success -> {
                            hideLoadingIndicator()

                                val order = result.data?.orders?.get(0)
                                val productImage = (order?.note ?: "").split("|##|").filter { it.isNotEmpty() }
                                binding.orderIdTv.text = "#${order?.order_number}"
                                binding.phone.text = order?.billing_address?.phone.toString()

                                binding.location.text =
                                    order?.billing_address?.address1 + ", " +
                                            order?.billing_address?.address2
                                binding.paymentStatus.text=order?.financial_status


                                val orderSubTotal =
                                    order?.subtotal_price?.toDoubleOrNull()?:0.0

                                binding.subTotal.text =
                                   LocalDataSourceImpl.getPriceAndCurrency(orderSubTotal)

                                val totalTax =
                                    order?.total_tax?.toDoubleOrNull()?:0.0
                                binding.totalTax.text =
                                  LocalDataSourceImpl.getPriceAndCurrency(totalTax)

                                val totalPrice =
                                    order?.total_price?.toDoubleOrNull()
                                        ?: 0.0
                                binding.totalCost.text =
                                  LocalDataSourceImpl.getPriceAndCurrency(totalPrice)


                                setupProductRecyclerView(
                                    order?.line_items!!.toMutableList(),
                                    productImage
                                )
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

    private fun setupProductRecyclerView(
        item: MutableList<LineItem>,
        productImage:List<String>
    ) {
        val orderProductsAdapter = OrderProductsAdapter(item, { onProductClick ->
            val action =
                OrderDetailsFragmentDirections.actionOrderDetailsFragmentToProductDetailsFragment(
                    onProductClick.product_id
                )
            findNavController().navigate(action)
        },requireContext(), productImage)
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL

        binding.productItemsRv.apply {
            adapter = orderProductsAdapter
            layoutManager = manager
        }
        orderProductsAdapter.removeFirstItem()
    }

    override fun onResume() {
        super.onResume()
        super.onResume()
        selectedCurrency = LocalDataSourceImpl.getCurrencyText(requireContext())
        LocalDataSourceImpl.saveCurrencyText(requireContext(), selectedCurrency)

    }
}