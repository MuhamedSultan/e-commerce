package com.example.e_commerce_app.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    lateinit var binding : FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ordersSettingLayout.setOnClickListener{
            val action = SettingFragmentDirections.actionSettingFragmentToOrdersFragment()
            findNavController().navigate(action)
        }
        binding.addressSettingLayout.setOnClickListener{
            //todo map
            val action = SettingFragmentDirections.actionSettingFragmentToMapFragment()
            findNavController().navigate(action)
        }
        binding.favoritesSettingLayout.setOnClickListener{
            //todo go to fav
            val action = SettingFragmentDirections.actionSettingFragmentToOrdersFragment()
            findNavController().navigate(action)
        }
        binding.logoutSettingLayout.setOnClickListener{
            // todo logout
            val action = SettingFragmentDirections.actionSettingFragmentToOrdersFragment()
            findNavController().navigate(action)
        }

    }

}