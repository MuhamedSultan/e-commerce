package com.example.e_commerce_app.product_details.view

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce_app.R
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.model.cart.CustomerDraftRequest
import com.example.e_commerce_app.model.cart.DraftOrderDetailsRequest
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.LineItems
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.product_details.adapter.ColorAdapter
import com.example.e_commerce_app.product_details.adapter.SizeAdapter
import com.example.e_commerce_app.product_details.adapter.ImageAdapter
import com.example.e_commerce_app.product_details.viewmodel.ProductDetailsViewModel
import com.example.e_commerce_app.product_details.viewmodel.ProductDetailsViewModelFactory
import com.example.e_commerce_app.util.ApiState
import com.example.e_commerce_app.util.ZoomOutPageTransformer
import kotlinx.coroutines.launch

class ProductDetailsFragment : Fragment() {

    private var productId: Long? = null
    private lateinit var viewModel: ProductDetailsViewModel
    private lateinit var colorAdapter: ColorAdapter
    private lateinit var sizeAdapter: SizeAdapter
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var colorRecyclerView: RecyclerView
    private lateinit var sizeRecyclerView: RecyclerView
    private lateinit var imageViewPager: ViewPager2
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getLong("productId")
        }

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        val shopifyDao = ShopifyDB.getInstance(requireContext()).shopifyDao()
        val localDataSource = LocalDataSourceImpl(shopifyDao)
        val repo = ShopifyRepoImpl(RemoteDataSourceImpl(), localDataSource)
        val factory = ProductDetailsViewModelFactory(repo, sharedPreferences)
        viewModel = ViewModelProvider(this, factory)[ProductDetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)

        colorRecyclerView = view.findViewById(R.id.productColorRecyclerView)
        sizeRecyclerView = view.findViewById(R.id.productSizeRecyclerView)
        imageViewPager = view.findViewById(R.id.productImageViewPager)

        colorRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        sizeRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        productId?.let {
            viewModel.fetchProductDetails(it)
        }

        observeViewModel()

        return view
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.productState.collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        state.data?.let { product ->
                            updateUI(product)
                        } ?: showError("Product data is null")
                    }

                    is ApiState.Error -> {
                        val errorMessage = state.message ?: "An unknown error occurred"
                        showError(errorMessage)
                    }

                    is ApiState.Loading -> {

                    }
                }
            }
        }
    }

    private fun updateUI(product: Product) {
        view?.findViewById<TextView>(R.id.productTitle)?.text = product.title
        view?.findViewById<TextView>(R.id.productDescription)?.text = product.body_html
        view?.findViewById<TextView>(R.id.productPrice)?.text =
            product.variants.firstOrNull()?.price ?: "$0.00"


        val addToCartButton = view?.findViewById<Button>(R.id.btn_add_to_cart)

        val favoriteButton = view?.findViewById<Button>(R.id.btn_add_to_favorite)

        addToCartButton?.setOnClickListener {
            val variant = product.variants.firstOrNull()
            if (variant != null) {
                val lineItem = LineItems(
                    quantity = 1,
                    price = variant.price.toDouble(),
                    title = product.title,
                    product_id = product.id.toString(),
                    variant_id = variant.id
                )

                val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
                val customerDraftRequest = if (shopifyCustomerId != null) {
                    CustomerDraftRequest(id = shopifyCustomerId.toLong())
                } else {
                    CustomerDraftRequest(id = -1)
                }

                val draftOrderDetailsRequest = DraftOrderDetailsRequest(
                    line_items = listOf(lineItem),
                    customer = customerDraftRequest
                )

                val draftOrderRequest = DraftOrderRequest(
                    draft_order = draftOrderDetailsRequest
                )
                lifecycleScope.launch {
                    viewModel.addToCart(draftOrderRequest)
                    Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show()
                    Log.d("ProductDetailsFragment", "Product added to cart: ${product.title}")
                }
            } else {
                Toast.makeText(context, "No variant available for this product", Toast.LENGTH_SHORT)
                    .show()
            }
        }




        favoriteButton?.setOnClickListener {
            lifecycleScope.launch {
                viewModel.addToFavorite(product)
                Toast.makeText(context, "Product added to favorite", Toast.LENGTH_SHORT).show()
                favoriteButton.setBackgroundResource(R.drawable.favfill)
            }
        }

        val colors = product.options.find { it.name == "Color" }?.values ?: emptyList()
        val sizes = product.options.find { it.name == "Size" }?.values ?: emptyList()

        colorAdapter = ColorAdapter(colors)
        sizeAdapter = SizeAdapter(sizes)

        colorRecyclerView.adapter = colorAdapter
        sizeRecyclerView.adapter = sizeAdapter

        val images = product.images
        imageAdapter = ImageAdapter(images)

        imageViewPager.adapter = imageAdapter
        imageViewPager.setPageTransformer(ZoomOutPageTransformer())
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
