package com.example.e_commerce_app.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.FragmentCartBinding
import com.example.e_commerce_app.databinding.FragmentPaymentBinding


class PaymentFragment : Fragment() {
    lateinit var binding : FragmentPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        binding.addCreditCard.setOnClickListener{
            val action = PaymentFragmentDirections.actionPaymentFragmentToCreditCardFragment()
            findNavController().navigate(action)
        }
    }
}