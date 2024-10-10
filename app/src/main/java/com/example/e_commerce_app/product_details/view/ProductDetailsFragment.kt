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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.R
import com.example.e_commerce_app.cart.DraftOrderManager
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.model.cart.CustomerId
import com.example.e_commerce_app.model.cart.DraftOrder
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
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    private var conversionRate: Double? = 0.0
    private lateinit var selectedCurrency: String

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
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)

        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
        selectedCurrency = LocalDataSourceImpl.getCurrencyText(requireContext())

        LocalDataSourceImpl.saveCurrencyText(requireContext(), selectedCurrency)
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
                            viewModel.currencyRates.collect { ratesState ->
                                when (ratesState) {
                                    is ApiState.Success -> {
                                        conversionRate = when (selectedCurrency) {
                                            "USD" -> ratesState.data?.rates?.USD
                                            "EUR" -> ratesState.data?.rates?.EUR
                                            "EGP" -> ratesState.data?.rates?.EGP
                                            else -> 0.0
                                        }
                                        updateUI(product)
                                    }
                                    is ApiState.Error ->{}
                                    is ApiState.Loading ->{
                                    }
                                }
                            }
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
        val price = product.variants.firstOrNull()?.price?.toDoubleOrNull() ?: 0.0
        val convertedPrice = conversionRate?.let { price * it } ?: price

        val formattedPrice = String.format("%.2f", convertedPrice)
        view?.findViewById<TextView>(R.id.productPrice)?.text = "$formattedPrice $selectedCurrency"
        val seeRatingTextView = view?.findViewById<TextView>(R.id.seeRating)

        val addToCartButton = view?.findViewById<Button>(R.id.btn_add_to_cart)

        val favoriteButton = view?.findViewById<Button>(R.id.btn_add_to_favorite)

        seeRatingTextView?.setOnClickListener {
            val action =
                ProductDetailsFragmentDirections.actionProductDetailsFragmentToRatingFragment()
            findNavController().navigate(action)
        }


        addToCartButton?.setOnClickListener {
            val variant = product.variants.firstOrNull()
            if (variant != null) {
                val lineItem = LineItems(
                    quantity = 1,
                    price = variant.price,
                    title = product.title,
                    productId = product.id.toString(),
                    variantId = variant.id.toString(),
                )
                var shp = SharedPrefsManager.getInstance()
                val customerId = shp.getShopifyCustomerId()
                val draftOrderId = shp.getDraftedOrderId()
                viewModel.addProductToDraftOrder(
                    draftOrderRequest =
                    DraftOrderManager.getInstance()
                        .addProductToDraftOrder(lineItem, product.image.src),
                    draftOrderId = draftOrderId ?: 0
                )
                observeViewModel()
                /*val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
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
                    //viewModel.addToCart(draftOrderRequest)
                    Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show()
                    Log.d("ProductDetailsFragment", "Product added to cart: ${product.title}")
                }*/
            } else {
                Toast.makeText(context, "No variant available for this product", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        lifecycleScope.launch {
            val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)

            if (shopifyCustomerId != null) {
                val isFavorite = viewModel.isProductFavorite(product.id, shopifyCustomerId)
                favoriteButton?.setBackgroundResource(if (isFavorite) R.drawable.ic_favourite_fill else R.drawable.ic_favourite_border)
            }

            favoriteButton?.setOnClickListener {
                lifecycleScope.launch {
                    val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
                    if (shopifyCustomerId != null) {

                        val isCurrentlyFavorite =
                            viewModel.isProductFavorite(product.id, shopifyCustomerId)
                        if (isCurrentlyFavorite) {
                            viewModel.removeFavorite(product, shopifyCustomerId)
                            favoriteButton.setBackgroundResource(R.drawable.ic_favourite_border)
                            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT)
                                .show()
                            LocalDataSourceImpl.setProductFavoriteStatus(
                                requireContext(),
                                product.id.toString(),
                                false
                            )
                        } else {
                            viewModel.addToFavorite(product, shopifyCustomerId)
                            favoriteButton.setBackgroundResource(R.drawable.ic_favourite_fill)
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

    private fun isProductFavorite(productId: Long, shopifyCustomerId: String): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        viewModel.fetchCurrencyRates()
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currencyRates.collect {
                    conversionRate = when (selectedCurrency) {
                        "USD" -> it.data?.rates?.USD
                        "EUR" -> it.data?.rates?.EUR
                        "EGP" -> it.data?.rates?.EGP
                        else -> null
                    }
                }

            }
        }
    }

}


