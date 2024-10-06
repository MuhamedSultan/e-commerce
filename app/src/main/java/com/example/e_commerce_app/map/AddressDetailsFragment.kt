package com.example.e_commerce_app.map

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.model.address.AddressResponse
import java.util.Locale

class AddressDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_address_details, container, false)
        val args = AddressDetailsFragmentArgs.fromBundle(requireArguments())

        val latitude = args.latitude
        val longitude = args.longitude
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
        if (addresses != null && !addresses.isEmpty()) {
            val address = addresses[0]
            val city = address.locality ?: "Unknown City"
            //var addressResponse = AddressResponse("","${address.locality}")
            view.findViewById<TextView>(R.id.tv_location_details).text =
                "Location: Latitude: $latitude, \nLongitude: $longitude ,\n" +
                        "city : $city\n${address.adminArea}\n${address.countryCode}\n" +
                        "${address.postalCode}\n${address.subAdminArea}\n${address.countryName}\n" +
                        "${address.premises}\n${address.subLocality}\n${address.featureName}\n" +
                        "${address.thoroughfare}\n${address.subThoroughfare}\n${address.url}\n"
        }


        return view
    }
}
