package com.example.e_commerce_app.cart.view

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.e_commerce_app.R
import com.example.e_commerce_app.cart.DraftOrderManager
import com.example.e_commerce_app.cart.viewmodel.CartViewModel
import com.example.e_commerce_app.cart.viewmodel.CartViewModelFactory
import com.example.e_commerce_app.databinding.FragmentAddingCouponBinding
import com.example.e_commerce_app.databinding.FragmentHomeBinding
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.model.cart.AppliedDiscount
import com.example.e_commerce_app.model.cart.DraftOrder
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.PriceRule
import com.example.e_commerce_app.model.cart.PriceRuleResponse
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.payment.PaymentActivity
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.math.abs

class AddingCouponFragment : Fragment() {
    private lateinit var binding:FragmentAddingCouponBinding
    private lateinit var viewModel: CartViewModel
    private lateinit var CouponsList: List<PriceRule>
    private lateinit var totalPrice: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = ShopifyRepoImpl(RemoteDataSourceImpl())
        val factory = CartViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[CartViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getAllCoupons()
        val draftOrderId = SharedPrefsManager.getInstance().getDraftedOrderId() ?:0
        viewModel.getProductsFromDraftOrder(draftOrderId)
        observeInitData()
        binding = FragmentAddingCouponBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setOnClickListener{
//            val action = AddingCouponFragmentDirections.actionAddingCouponFragmentToPaymentFragment(totalPrice)
//            findNavController().navigate(action)
            // Create an Intent to start PaymentActivity
            val intent = Intent(requireContext(), PaymentActivity::class.java)

            // You can pass the totalPrice as an extra to the intent
            intent.putExtra("TOTAL_PRICE", totalPrice)

            // Start the PaymentActivity
            startActivity(intent)
        }
        binding.btnApply.setOnClickListener{
            val CouponeTitle = binding.etCouponCode.text.toString()
            disableUi()
            for(coupon in CouponsList){
                if(coupon.title==CouponeTitle){
                    binding.couponStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.couponStatus.text = "Coupon applied!"
                    val appliedDiscount =AppliedDiscount(
                        id = coupon.id,
                        title = coupon.title,
                        valueType = coupon.value_type,
                        value = abs(coupon.value.toDouble()).toString(),
                        description = "Custom Discount"
                    )
                    Log.i("TAG", "The applied Discount: $appliedDiscount")
                    viewModel.addCouponToDraftOrder(
                            DraftOrderManager.getInstance().addCouponToDraftOrder(appliedDiscount = appliedDiscount),
                        SharedPrefsManager.getInstance().getDraftedOrderId()?:0
                    )
                    break
                }else{
                    binding.couponStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.couponStatus.text = "Invalid coupon!"
                    enableUi()
                }
            }
        }
    }


    private fun observeInitData() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.draftOrderState.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            binding.addCouponLayout.visibility = View.GONE
                            binding.loadingIndicator.visibility = View.VISIBLE
                        }

                        is ApiState.Success -> {
                            binding.addCouponLayout.visibility = View.VISIBLE
                            binding.loadingIndicator.visibility = View.GONE
                            enableUi()
                            result.data?.let { draftOrderResponse ->
                                binding.tvTax.text = draftOrderResponse.draft_order.total_tax
                                binding.tvSubtotal.text = draftOrderResponse.draft_order.subtotal_price
                                binding.tvTotal.text = draftOrderResponse.draft_order.total_price
                                totalPrice = draftOrderResponse.draft_order.total_price.toString()
                            }
                        }

                        is ApiState.Error -> {
                            binding.addCouponLayout.visibility = View.VISIBLE
                            binding.loadingIndicator.visibility = View.GONE
                            Log.e("TAG", "observeDraftOrderId: ${result.message}", )
                            showError(result.message.toString())
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allCouponsResult.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                        }

                        is ApiState.Success -> {
                            result.data?.let { collections ->
                                CouponsList = collections.price_rules
                            }
                        }

                        is ApiState.Error -> {
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
    private fun disableUi() {
        binding.btnApply.isEnabled = false
        binding.btnSubmit.isEnabled = false
        binding.btnApply.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_not_very_dark_color)))
        binding.btnSubmit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_not_very_dark_color)))
    }
    private fun enableUi() {
        binding.btnApply.isEnabled = true
        binding.btnSubmit.isEnabled = true
        binding.btnApply.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.basic_color)))
        binding.btnSubmit.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.basic_color)))
    }


//    private fun showLoadingIndicator() {
//        binding.loadingIndicator.visibility = View.VISIBLE
//        binding.groupLayout.visibility = View.GONE
//    }
//
//    private fun hideLoadingIndicator() {
//        binding.loadingIndicator.visibility = View.GONE
//        binding.dotsIndicator.visibility = View.VISIBLE
//        binding.couponPager.visibility = View.VISIBLE
//        binding.groupLayout.visibility = View.VISIBLE
//    }

}