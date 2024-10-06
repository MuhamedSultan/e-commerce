package com.example.e_commerce_app.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.e_commerce_app.R
class AddressDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_address_details, container, false)
        val args = AddressDetailsFragmentArgs.fromBundle(requireArguments())

        val latitude = args.latitude
        val longitude = args.longitude

        view.findViewById<TextView>(R.id.tv_location_details).text =
            "Location: Latitude: $latitude, Longitude: $longitude"

        return view
    }
}
