package com.example.e_commerce_app.map.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.databinding.ItemAddressBinding
import com.example.e_commerce_app.map.viewModel.AddressViewModel
import com.example.e_commerce_app.model.address.AddressResponseModel

class AddressAdapter(val viewModel: AddressViewModel,private val onAddressSelected: (AddressResponseModel , AddressResponseModel) -> Unit) :
    ListAdapter<AddressResponseModel, AddressAdapter.AddressHolder>(AddressDiffUtilItem()) {

    private var selectedPosition = RecyclerView.NO_POSITION
    lateinit var context : Context
    lateinit var defaultAddress : AddressResponseModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        context=parent.context
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemAddressBinding.inflate(inflater, parent, false)
        return AddressHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val address = getItem(position)
        holder.bind(address)
        for (add in currentList){
            if (add.default==true){
                defaultAddress = add
            }
        }
        // Highlight the selected address
        holder.binding.root.isSelected = selectedPosition == position

        // Set click listener
        holder.binding.root.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)  // Unhighlight the previous selection
            notifyItemChanged(selectedPosition)  // Highlight the new selection

            // Trigger the selection callback
            onAddressSelected(address,defaultAddress)
        }
        holder.binding.btnDeleteAddress.setOnClickListener {
            if(address.default == true) {
                Toast.makeText(context , "Can't delete Default Address" , Toast.LENGTH_SHORT).show()
            }else{
                removeItem(holder.adapterPosition)
            }
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
    private fun removeItem(position: Int) {
        val mutableList = currentList.toMutableList()
        viewModel.deleteAddress(mutableList.get(position).id)
        mutableList.removeAt(position)
        submitList(mutableList)  // Submit updated list to the adapter
    }
}
