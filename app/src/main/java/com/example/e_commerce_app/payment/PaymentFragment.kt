package com.example.e_commerce_app.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.e_commerce_app.R
import com.example.e_commerce_app.cart.DraftOrderManager
import com.example.e_commerce_app.cart.viewmodel.CartViewModel
import com.example.e_commerce_app.cart.viewmodel.CartViewModelFactory
import com.example.e_commerce_app.databinding.FragmentCartBinding
import com.example.e_commerce_app.databinding.FragmentPaymentBinding
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.map.AddressDetailsFragmentArgs
import com.example.e_commerce_app.model.cart.CustomerId
import com.example.e_commerce_app.model.cart.DraftOrder
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.LineItems
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.payment.viewModel.PaymentViewModel
import com.example.e_commerce_app.payment.viewModel.PaymentViewModelFactory
import com.example.e_commerce_app.setting.SettingFragmentDirections
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import kotlinx.coroutines.launch
import java.math.BigDecimal


class PaymentFragment : Fragment() {
    lateinit var binding : FragmentPaymentBinding
    private lateinit var viewModel: PaymentViewModel
    lateinit var totalPrice : String
    private val PAYPAL_REQUEST_CODE = 123

    // PayPal configuration
    private val config = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // Use ENVIRONMENT_PRODUCTION for live payments
        .clientId("AVGCA-t76v87mfeQ5xe5h_yAD0xJEWWijGIDIGJ_xytjUVvp7nBZodsFpmb2PMJZX8anfBb7I1S9pgPZ") // Replace with your client ID


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = PaymentFragmentArgs.fromBundle(requireArguments())
        totalPrice = args.price

        val shopifyDao = ShopifyDB.getInstance(requireContext()).shopifyDao()
        val repo = ShopifyRepoImpl(RemoteDataSourceImpl())
        val factory = PaymentViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[PaymentViewModel::class.java]
        // Start the PayPal Service
        val intent = Intent(context, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        context?.startService(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*binding.confirmPaymentButton.setOnClickListener{
            val action = PaymentFragmentDirections.actionPaymentFragmentToCreditCardFragment()
            findNavController().navigate(action)
        }*/
        binding.confirmPaymentButton.setOnClickListener {
            // Get the selected radio button from the RadioGroup
            val selectedPaymentMethodId = binding.radioGroup.checkedRadioButtonId
            val selectedPaymentMethod = view.findViewById<RadioButton>(selectedPaymentMethodId)

            when (selectedPaymentMethod.text.toString()) {
                "Cash on Delivery" -> {
                    viewModel.CreateOrder(paymentPending = true)
                    observeDraftOrderId()
                    Log.i("TAG", "onViewCreated: ")
                }
                "PayPal" -> {
                    processPayment()
                }
            }
        }
    }

    // PayPal Part
    private fun processPayment() {
        // Create a payment
        val payment = PayPalPayment(
            BigDecimal(totalPrice), "USD", "Sample Item",
            PayPalPayment.PAYMENT_INTENT_SALE)

        // Create intent to start PaymentActivity
        val intent = Intent(context, PaymentActivity::class.java)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment) // Pass the payment
        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val confirm = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                confirm?.let {
                    Toast.makeText(requireContext(), "Payment Success", Toast.LENGTH_SHORT).show()
                    viewModel.CreateOrder(paymentPending = false)
                    observeDraftOrderId()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(requireContext(), "Payment canceled", Toast.LENGTH_SHORT).show()
                // Handle canceled payment
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(requireContext(), "Payment invalid", Toast.LENGTH_SHORT).show()
                // Handle invalid payment
            }
        }
    }
    private fun observeDraftOrderId() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.creatingDraftOrder.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            showLoadingIndicator()
                        }

                        is ApiState.Success -> {
                            hideLoadingIndicator()
                            result.data?.let { collections ->
                                SharedPrefsManager.getInstance().setDraftedOrderId(collections.draft_order.id)
                                DraftOrderManager.init(
                                    DraftOrderRequest(
                                    draftOrder = DraftOrder(
                                        lineItems = mutableListOf(
                                            LineItems(
                                                title = "m",
                                                price = "0.00",
                                                quantity = 1,
                                                productId = "12",
                                                variantId = null
                                            )
                                        ),
                                        appliedDiscount = null,
                                        customer = CustomerId(id = collections.draft_order.customer.id),
                                        useCustomerDefaultAddress = true
                                    )
                                )
                                )
                                val action = PaymentFragmentDirections.actionPaymentFragmentToHomeFragment()
                                findNavController().navigate(action)
                                Log.i("TAG", "getDraftOrderSaveInShP: ${collections.draft_order.id}")
                            }
                        }

                        is ApiState.Error -> {
                            hideLoadingIndicator()
                            Log.e("TAG", "observeDraftOrderId: ${result.message}", )
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
        binding.paymentPage.visibility = View.GONE
    }

    private fun hideLoadingIndicator() {
        binding.loadingIndicator.visibility = View.GONE
        binding.paymentPage.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the PayPal Service
        val intent = Intent(context, PayPalService::class.java)
        context?.stopService(intent)
    }
}