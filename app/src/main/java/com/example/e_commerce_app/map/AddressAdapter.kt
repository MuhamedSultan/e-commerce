package com.example.e_commerce_app.map

import android.content.Context
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.databinding.ItemAddressBinding
import com.example.e_commerce_app.model.address.Address
import com.example.e_commerce_app.model.address.AddressResponse
import com.example.e_commerce_app.model.address.AddressResponseModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
class AddressAdapter(private val onAddressSelected: (AddressResponseModel) -> Unit) :
    ListAdapter<AddressResponseModel, AddressAdapter.AddressHolder>(AddressDiffUtilItem()) {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemAddressBinding.inflate(inflater, parent, false)
        return AddressHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val address = getItem(position)
        holder.bind(address)

        // Highlight the selected address
        holder.binding.root.isSelected = selectedPosition == position

        // Set click listener
        holder.binding.root.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)  // Unhighlight the previous selection
            notifyItemChanged(selectedPosition)  // Highlight the new selection

            // Trigger the selection callback
            onAddressSelected(address)
        }
    }

    class AddressHolder(val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: AddressResponseModel) {
            binding.tvAddressCountryName.text = address.address2
            binding.tvAddressDetails.text = address.address1
            binding.tvAddressPhone.text = address.phone
        }
    }

    class AddressDiffUtilItem : DiffUtil.ItemCallback<AddressResponseModel>() {
        override fun areItemsTheSame(oldItem: AddressResponseModel, newItem: AddressResponseModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AddressResponseModel, newItem: AddressResponseModel): Boolean {
            return oldItem == newItem
        }
    }
}

/*class AddressAdapter() :
    ListAdapter<AddressResponseModel, AddressAdapter.AddressHolder>(AddressDiffUtilItem()) {
    lateinit var binding : ItemAddressBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemAddressBinding.inflate(inflater , parent ,false)
        return AddressHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val address = getItem(position)
        binding.tvAddressCountryName.text = address.address2
        binding.tvAddressDetails.text = address.address1
        // Format the phone number using PhoneNumberUtils.formatNumber
        binding.tvAddressPhone.text = address.phone

    }

    class AddressHolder(var binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root)


    class AddressDiffUtilItem : DiffUtil.ItemCallback<AddressResponseModel>() {
        override fun areItemsTheSame(oldItem: AddressResponseModel, newItem: AddressResponseModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AddressResponseModel, newItem: AddressResponseModel): Boolean {
            return oldItem == newItem
        }

    }

}*/
/*
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
}*/
