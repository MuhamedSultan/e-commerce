package com.example.e_commerce_app.map

import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.databinding.ItemAddressBinding
import com.example.e_commerce_app.model.address.Address
import java.util.*

class AddressAdapter(private val addressList: List<Address>) :
    RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addressList[position])
    }

    override fun getItemCount(): Int = addressList.size

    inner class AddressViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address) {
            binding.tvAddressCountryName.text = address.country
            binding.tvAddressDetails.text = address.address
            // Format the phone number using PhoneNumberUtils.formatNumber
            binding.tvAddressPhone.text = PhoneNumberUtils.formatNumber(address.phone, Locale.getDefault().country)
        }
    }
}
