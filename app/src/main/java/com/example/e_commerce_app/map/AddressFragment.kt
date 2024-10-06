package com.example.e_commerce_app.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_app.databinding.FragmentAddressBinding
import com.example.e_commerce_app.map.AddressAdapter
import com.example.e_commerce_app.model.address.Address

class AddressFragment : Fragment() {
    private lateinit var binding: FragmentAddressBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addressList = listOf(
            Address(1, 30.0444, 31.2357, "Egypt", "123 Main St, Cairo", "201018870021"),
            Address(2, 51.5074, -0.1278, "UK", "456 Elm St, London", "447911123456")
        )

        binding.rvAddresses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddresses.adapter = AddressAdapter(addressList)
    }
}
