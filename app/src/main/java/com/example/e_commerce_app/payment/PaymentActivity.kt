package com.example.e_commerce_app.payment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.R
import com.example.e_commerce_app.cart.DraftOrderManager
import com.example.e_commerce_app.databinding.ActivityMainBinding
import com.example.e_commerce_app.databinding.ActivityPaymentBinding
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.model.cart.CustomerId
import com.example.e_commerce_app.model.cart.DraftOrder
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.LineItems
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.payment.viewModel.PaymentViewModel
import com.example.e_commerce_app.payment.viewModel.PaymentViewModelFactory
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.UUID

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var viewModel: PaymentViewModel
    lateinit var totalPrice : String
    private lateinit var payPalRepository: PayPalRepository
    private val returnUrl = "com.devhiv.paypaltest://demoapp"
    private var orderId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val shopifyDao = ShopifyDB.getInstance(this).shopifyDao()
        val repo = ShopifyRepoImpl(RemoteDataSourceImpl())
        val factory = PaymentViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[PaymentViewModel::class.java]
        totalPrice = intent.getStringExtra("TOTAL_PRICE")?:"1.0"

        payPalRepository = PayPalRepository(
//            clientID = "AQSo4-8c09dCj6c-SU8c_5dmxfpDeOqkgoRwqmI80ZxNYMuwciCLnf6k1z_X2niaNNwHPyA67OuUxQBl",
//            secretID = "ECe4BSmb5wSs57wrq-hN94TJRurRrovSPfjJqRmq1Sxdm40sx6vPU--vZIA1xXQSBSOB5nEZ3obHtKjG"
            clientID = "AVGCA-t76v87mfeQ5xe5h_yAD0xJEWWijGIDIGJ_xytjUVvp7nBZodsFpmb2PMJZX8anfBb7I1S9pgPZ",
            secretID = "EHXdPi2SBNS427-LyCgsfjINOqh43C559f-VceKHX85zAuznxAOS60X4M6qS35Lnn39ubkGYWZQkb2hP"
        )


        // Initially hide the "Start Order" button


        // Fetch access token on start
        fetchAccessToken()

        // On clicking start order, initiate order creation
        binding.confirmPaymentButton.setOnClickListener {
            // Get the selected radio button from the RadioGroup
            val selectedPaymentMethodId = binding.radioGroup.checkedRadioButtonId
            val selectedPaymentMethod = findViewById<RadioButton>(selectedPaymentMethodId)

            when (selectedPaymentMethod.text.toString()) {
                "Cash on Delivery" -> {
                    viewModel.CreateOrder(paymentPending = true)
                    observeDraftOrderId()
                    Log.i("TAG", "onViewCreated: ")
                }
                "PayPal" -> {
                    //processPayment()
                    startOrder()
                }
            }
        }
    }

    // Coroutine Exception Handler
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception: ${throwable.localizedMessage}")
        Toast.makeText(this, "Error: ${throwable.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    // Function to fetch PayPal Access Token
    private fun fetchAccessToken() {
        lifecycleScope.launch(handler) {
            try {
                var paidStatus : Boolean = SharedPrefsManager.getInstance().getPaidStatus()
                if (paidStatus == false) {
                    val token = payPalRepository.fetchAccessToken()
                    Toast.makeText(
                        this@PaymentActivity,
                        "Access Token Fetched!",
                        Toast.LENGTH_SHORT
                    ).show()
                    SharedPrefsManager.getInstance().setPaidStatus(true)
                    // Show the "Start Order" button once the token is fetched
                    binding.confirmPaymentButton.visibility = View.VISIBLE
                }else{
                    SharedPrefsManager.getInstance().setPaidStatus(false)
                    viewModel.CreateOrder(paymentPending = false)
                    observeDraftOrderId()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PaymentActivity, "Failed to fetch access token: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to start creating an order
    private fun startOrder() {
        val uniqueId = UUID.randomUUID().toString()

        // Construct the order request payload
        val orderRequest = OrderRequest(
            purchase_units = listOf(
                PurchaseUnit(
                    reference_id = uniqueId,
                    amount = Amount(currency_code = "USD", value = totalPrice)
                )
            ),
            payment_source = PaymentSource(
                paypal = PayPalExperience(
                    experience_context = ExperienceContext(
                        payment_method_preference = "IMMEDIATE_PAYMENT_REQUIRED",
                        brand_name = "Couture-Corner",
                        locale = "en-US",
                        landing_page = "LOGIN",
                        shipping_preference = "NO_SHIPPING",
                        user_action = "PAY_NOW",
                        return_url = returnUrl,
                        cancel_url = "https://example.com/cancelUrl"
                    )
                )
            )
        )

        lifecycleScope.launch(handler) {
            try {
                var response = payPalRepository.createOrder(orderRequest)
                orderId=response.id
                val approvalLink = response.links.get(1).href

                if (!approvalLink.isNullOrEmpty()) {
                    Log.i(TAG, "Approval Link: $approvalLink")
                    // Redirect the user to the approval link
                    openApprovalUrl(approvalLink)
                } else {
                    Log.e(TAG, "Approval link not found in the response.")
                    Toast.makeText(this@PaymentActivity, "Approval link not found.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PaymentActivity, "Error creating order: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to open the approval URL
    private fun openApprovalUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: $intent")

        intent?.data?.let { uri ->
            val opType = uri.getQueryParameter("opType")
            val token = uri.getQueryParameter("token")
            val payerId = uri.getQueryParameter("PayerID")

            Log.d(TAG, "Operation Type: $opType, Token: $token, PayerID: $payerId")

            when (opType) {
                "payment" -> {
                    if (token != null && payerId != null) {
                        captureOrder(token, payerId)
                    } else {
                        Log.e(TAG, "Token or PayerID is null.")
                    }
                }
                "cancel" -> Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
                else -> Log.e(TAG, "Unknown operation type: $opType")
            }
        } ?: run {
            Log.e(TAG, "Intent data is null.")
        }

    }



    // Function to capture an order
    private fun captureOrder(token: String, payerId: String) {
        lifecycleScope.launch(handler) {
            try {
                val capturedOrderId = payPalRepository.captureOrder(orderId, "10.00", token, payerId)
                Toast.makeText(this@PaymentActivity, "Order Captured! ID: $capturedOrderId", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@PaymentActivity, "Error capturing order: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun observeDraftOrderId() {
        lifecycleScope.launch {
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
                            /*val intent = Intent()
                            intent.putExtra("PAYMENT_SUCCESS", true)  // Pass any relevant data
                            setResult(RESULT_OK, intent)
                            finish()*/
                            val intent = Intent(this@PaymentActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            /*val action = PaymentFragmentDirections.actionPaymentFragmentToHomeFragment()
                            findNavController().navigate(action)*/
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
    private fun showError(message: String) {
        Toast.makeText(this@PaymentActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingIndicator() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.paymentPage.visibility = View.GONE
    }

    private fun hideLoadingIndicator() {
        binding.loadingIndicator.visibility = View.GONE
        binding.paymentPage.visibility = View.VISIBLE
    }

    companion object {
        const val TAG = "PayPalExample"
    }
}