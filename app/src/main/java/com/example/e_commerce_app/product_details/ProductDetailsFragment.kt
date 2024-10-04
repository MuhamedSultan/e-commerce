package com.example.e_commerce_app.product_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.network.Network
import com.example.e_commerce_app.product_details.adapter.ColorAdapter
import com.example.e_commerce_app.product_details.adapter.SizeAdapter
import com.example.e_commerce_app.product_details.adapter.ImageAdapter // Import the ImageAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailsFragment : Fragment() {
    private var productId: Long? = null
    private lateinit var colorAdapter: ColorAdapter
    private lateinit var sizeAdapter: SizeAdapter
    private lateinit var imageAdapter: ImageAdapter // Declare ImageAdapter
    private lateinit var colorRecyclerView: RecyclerView
    private lateinit var sizeRecyclerView: RecyclerView
    private lateinit var imageRecyclerView: RecyclerView // Declare image RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getLong("productId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)

        colorRecyclerView = view.findViewById(R.id.productColorRecyclerView)
        sizeRecyclerView = view.findViewById(R.id.productSizeRecyclerView)
        imageRecyclerView = view.findViewById(R.id.productImageRecyclerView) // Initialize here

        colorRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        sizeRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        imageRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) // Set layout manager

        fetchProductDetails(productId)

        return view
    }

    private fun fetchProductDetails(productId: Long?) {
        productId?.let { id ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = Network.shopifyService.fetchProductById(id)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            response.body()?.let { singleProductResponse ->
                                val product = singleProductResponse.product
                                Log.d("ProductDetailsFragment", "Product found: ${product.title}")
                                updateUI(product)
                            } ?: run {
                                showError("Product response is null.")
                            }
                        } else {
                            showError("Error: ${response.code()} - ${response.message()}")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showError("An error occurred: ${e.message}")
                    }
                }
            }
        } ?: showError("Invalid Product ID.")
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun updateUI(product: Product) {
        view?.findViewById<TextView>(R.id.productTitle)?.text = product.title
        view?.findViewById<TextView>(R.id.productDescription)?.text = product.body_html
        view?.findViewById<TextView>(R.id.productPrice)?.text = product.variants.firstOrNull()?.price ?: "$0.00"

        // Get the color options and size options
        val colors = product.options.find { it.name == "Color" }?.values ?: emptyList()
        val sizes = product.options.find { it.name == "Size" }?.values ?: emptyList()

        // Update the adapters
        colorAdapter = ColorAdapter(colors)
        sizeAdapter = SizeAdapter(sizes)

        colorRecyclerView.adapter = colorAdapter
        sizeRecyclerView.adapter = sizeAdapter

        // Fetch and set images
        val images = product.images // Assuming images is a List<Image> in the Product model
        imageAdapter = ImageAdapter(images)
        imageRecyclerView.adapter = imageAdapter
    }
}
