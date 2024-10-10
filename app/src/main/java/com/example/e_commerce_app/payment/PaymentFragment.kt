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
import androidx.navigation.fragment.findNavController
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.FragmentCartBinding
import com.example.e_commerce_app.databinding.FragmentPaymentBinding
import com.example.e_commerce_app.map.AddressDetailsFragmentArgs
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import java.math.BigDecimal


class PaymentFragment : Fragment() {
    lateinit var binding : FragmentPaymentBinding
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
                    //todo update draft order status payment false
                    //todo complete draft order
                    //todo delete draft order and get new one

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
                    // Handle successful payment
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

    override fun onDestroy() {
        super.onDestroy()
        // Stop the PayPal Service
        val intent = Intent(context, PayPalService::class.java)
        context?.stopService(intent)
    }
}