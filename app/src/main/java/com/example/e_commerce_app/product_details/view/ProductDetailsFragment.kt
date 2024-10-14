package com.example.e_commerce_app.product_details.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.R
import com.example.e_commerce_app.cart.DraftOrderManager
import com.example.e_commerce_app.databinding.FragmentProductDetailsBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.db.ShopifyDB
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class ProductDetailsFragment : Fragment() {

    private var productId: Long? = null
    private lateinit var viewModel: ProductDetailsViewModel
    private lateinit var colorAdapter: ColorAdapter
    private lateinit var sizeAdapter: SizeAdapter
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var conversionRate: Double? = 0.0
    private lateinit var selectedCurrency: String
    private lateinit var progressBar: ProgressBar
    private lateinit var binding: FragmentProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getLong("productId")
        }

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        val shopifyDao = ShopifyDB.getInstance(requireContext()).shopifyDao()
        val localDataSource = LocalDataSourceImpl(shopifyDao)
        val repo = ShopifyRepoImpl(RemoteDataSourceImpl(), localDataSource)
        val factory = ProductDetailsViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[ProductDetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)

        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE

        selectedCurrency = LocalDataSourceImpl.getCurrencyText(requireContext())
        LocalDataSourceImpl.saveCurrencyText(requireContext(), selectedCurrency)

        binding.productColorRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.productSizeRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.productImageViewPager.setPageTransformer(ZoomOutPageTransformer())

        productId?.let { viewModel.fetchProductDetails(it) }

        progressBar = binding.progressBar2

        observeViewModel()

        return binding.root
    }


    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.productState.collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        progressBar.visibility = View.GONE
                        state.data?.let { product ->
                            progressBar.visibility = View.GONE
                            updateUI(product)

                        }
                    }

                    is ApiState.Error -> {
                        progressBar.visibility = View.GONE
                        val errorMessage = state.message ?: "An unknown error occurred"
                        showError(errorMessage)
                    }

                    is ApiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun updateUI(product: Product) {
        binding.productTitle.text = product.title
        binding.productDescription.text = product.body_html
        val price = product.variants.firstOrNull()?.price?.toDoubleOrNull() ?: 0.0
        val formattedPrice = LocalDataSourceImpl.getPriceAndCurrency(price)
        binding.productPrice.text = formattedPrice

        binding.seeRating.setOnClickListener {
            val action =
                ProductDetailsFragmentDirections.actionProductDetailsFragmentToRatingFragment()
            findNavController().navigate(action)
        }

        binding.btnAddToCart.setOnClickListener {
            val variant = product.variants.firstOrNull()
            if (variant != null) {
                val lineItem = LineItems(
                    quantity = 1,
                    price = variant.price,
                    title = product.title,
                    productId = product.id.toString(),
                    variantId = variant.id.toString(),
                )
                val shp = SharedPrefsManager.getInstance()
                val customerId = shp.getShopifyCustomerId()
                val draftOrderId = shp.getDraftedOrderId()
                viewModel.addProductToDraftOrder(
                    draftOrderRequest =
                    DraftOrderManager.getInstance()
                        .addProductToDraftOrder(lineItem, product.image.src),
                    draftOrderId = draftOrderId ?: 0
                )
                observeViewModel()
                Toast.makeText(context, "Added to cart successfully", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(context, "No variant available for this product", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        lifecycleScope.launch {
            val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
            if (shopifyCustomerId != null) {
                val isFavorite = viewModel.isProductFavorite(product.id, shopifyCustomerId)
                binding.btnAddToFavorite.setBackgroundResource(if (isFavorite) R.drawable.ic_favourite_fill else R.drawable.ic_favourite_border)
            }

            binding.btnAddToFavorite.setOnClickListener {
                lifecycleScope.launch {
                    val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
                    if (shopifyCustomerId != null) {
                        val isCurrentlyFavorite =
                            viewModel.isProductFavorite(product.id, shopifyCustomerId)
                        if (isCurrentlyFavorite) {
                            viewModel.removeFavorite(product, shopifyCustomerId)
                            binding.btnAddToFavorite.setBackgroundResource(R.drawable.ic_favourite_border)
                            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT)
                                .show()
                            LocalDataSourceImpl.setProductFavoriteStatus(
                                requireContext(),
                                product.id.toString(),
                                false
                            )
                        } else {
                            viewModel.addToFavorite(product, shopifyCustomerId)
                            binding.btnAddToFavorite.setBackgroundResource(R.drawable.ic_favourite_fill)
                            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
                            LocalDataSourceImpl.setProductFavoriteStatus(
                                requireContext(),
                                product.id.toString(),
                                true
                            )
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Please log in to add favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        val colors = product.options.find { it.name == "Color" }?.values ?: emptyList()
        val sizes = product.options.find { it.name == "Size" }?.values ?: emptyList()

        colorAdapter = ColorAdapter(colors)
        sizeAdapter = SizeAdapter(sizes)

        binding.productColorRecyclerView.adapter = colorAdapter
        binding.productSizeRecyclerView.adapter = sizeAdapter

        val images = product.images
        imageAdapter = ImageAdapter(images)

        binding.productImageViewPager.adapter = imageAdapter
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
    }

}


