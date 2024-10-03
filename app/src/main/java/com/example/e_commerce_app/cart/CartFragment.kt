package com.example.e_commerce_app.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    lateinit var binding : FragmentCartBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.checkoutBtn.setOnClickListener{
            val action = CartFragmentDirections.actionCartFragmentToPaymentFragment()
            findNavController().navigate(action)
        }
    }

}