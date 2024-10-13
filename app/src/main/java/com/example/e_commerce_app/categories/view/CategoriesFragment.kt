package com.example.e_commerce_app.categories.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_app.R
import com.example.e_commerce_app.categories.view.adapter.CategoriesAdapter
import com.example.e_commerce_app.categories.view.adapter.CategoriesProductsAdapter
import com.example.e_commerce_app.categories.view.adapter.OnCategoryClick
import com.example.e_commerce_app.categories.viewmodel.CategoriesViewModel
import com.example.e_commerce_app.categories.viewmodel.CategoriesViewModelFactory
import com.example.e_commerce_app.databinding.FragmentCategoriesBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.home.view.adapter.SuggestionsAdapter
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.currencyResponse.Rates
import com.example.e_commerce_app.model.custom_collection.CustomCollection
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState
import com.example.e_commerce_app.util.GuestUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class CategoriesFragment : Fragment(), OnCategoryClick {
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoriesViewModel: CategoriesViewModel
    private var allProducts: List<Product> = emptyList()
    private var collectionId: Long = 0
    private var currentSeekBarProgress: Int = 0
    private lateinit var menuItems: Array<ImageView>
    private var isOpen = false
    private var selectedProductType: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var suggestionsAdapter: SuggestionsAdapter
    private lateinit var selectedCurrency: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = ShopifyDB.getInstance(requireContext()).shopifyDao()
        val localDataSource = LocalDataSourceImpl(dao)
        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource, localDataSource)
        val factory = CategoriesViewModelFactory(repo)
        categoriesViewModel = ViewModelProvider(this, factory)[CategoriesViewModel::class.java]
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        selectedCurrency = LocalDataSourceImpl.getCurrencyText(requireContext())
        LocalDataSourceImpl.saveCurrencyText(requireContext(), selectedCurrency)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Set icon colors programmatically
        val filteringOptionsFAB = binding.filteringOptions
        filteringOptionsFAB.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )

        val shirtsTypeFAB = binding.shirtsType
        shirtsTypeFAB.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )

        val shoesTypeFAB = binding.shoesType
        shoesTypeFAB.setColorFilter(
            ContextCompat.getColor(requireContext(), android.R.color.white),
            PorterDuff.Mode.SRC_IN
        )

        val accessoriesTypeFAB = binding.accessoriesType
        accessoriesTypeFAB.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )

        val allProductsFAB = binding.allProducts
        allProductsFAB.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )

        // Set background color programmatically
        val backgroundColor = ContextCompat.getColor(
            requireContext(),
            R.color.basic_color
        )

        filteringOptionsFAB.setBackgroundColor(backgroundColor)
        shirtsTypeFAB.setBackgroundColor(backgroundColor)
        shoesTypeFAB.setBackgroundColor(backgroundColor)
        accessoriesTypeFAB.setBackgroundColor(backgroundColor)
        allProductsFAB.setBackgroundColor(backgroundColor)

        observeResults()

        setupSearchFunctionality()

    }

    private fun setupSearchFunctionality() {
        suggestionsAdapter = SuggestionsAdapter(emptyList()) { selectedProduct ->
            val action =
                CategoriesFragmentDirections.actionCategoriesFragmentToProductDetailsFragment(
                    selectedProduct.id
                )
            findNavController().navigate(action)
        }

        binding.suggestionsRv.adapter = suggestionsAdapter
        binding.suggestionsRv.layoutManager = LinearLayoutManager(requireContext())

        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    categoriesViewModel.searchProducts(query)
                } else {
                    binding.suggestionsRv.visibility = View.GONE
                }
            }
        })

        // Observe the filtered products
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                categoriesViewModel.filteredProducts.collect { filteredProducts ->
                    if (filteredProducts.isNotEmpty()) {
                        showSuggestions(filteredProducts)
                    } else {
//                        Toast.makeText(
//                            requireContext(),
//                            "No products found for query: ${binding.edSearch.text}",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        binding.suggestionsRv.visibility = View.GONE
                    }
                }
            }
        }
    }


    private fun showSuggestions(filteredProducts: List<Product>) {
        suggestionsAdapter.updateProducts(filteredProducts)
        binding.suggestionsRv.visibility = View.VISIBLE
    }


    private fun observeResults() {
        categoriesViewModel.getCategorise()
        categoriesViewModel.fetchCurrencyRates()
        categoriesViewModel.getProductsOfSelectedCategory(482200682811)

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                categoriesViewModel.categoriesResult.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            // Handle loading state
                        }

                        is ApiState.Success -> {
                            setupCategoriesRecyclerview(
                                result.data?.custom_collections ?: emptyList()
                            )
                        }

                        is ApiState.Error -> {
                            showError(result.message.toString())
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Combine productsResult and currencyRates to avoid nested collections
                combine(
                    categoriesViewModel.productsResult,
                    categoriesViewModel.currencyRates
                ) { productsResult, currencyRates ->
                    Pair(productsResult, currencyRates)
                }.collect { (productsResult, currencyRates) ->
                    when (productsResult) {
                        is ApiState.Loading -> {
                            showLoadingIndicator()
                        }

                        is ApiState.Success -> {
                            hideLoadingIndicator()
                            val currencyResponse = currencyRates.data ?: CurrencyResponse(
                                "",
                                "",
                                Rates(0.0, 0.0, 0.0),
                                true,
                                0
                            )
                            val conversionRate = when (selectedCurrency) {
                                "USD" -> currencyResponse.rates.USD
                                "EUR" -> currencyResponse.rates.EUR
                                "EGP" -> currencyResponse.rates.EGP
                                else -> 0.0
                            }

                            allProducts = productsResult.data?.products ?: emptyList()
                            categoriesViewModel.originalProducts = allProducts

                            Log.d("CategoriesFragment", "allProducts: ${allProducts.size}")

                            setupCategoriesProductsRecyclerview(
                                allProducts,
                                currencyResponse,
                                conversionRate
                            )
                            productTypeFiltering(currencyResponse, conversionRate)
                            priceFiltering(currencyResponse, conversionRate)
                        }

                        is ApiState.Error -> {
                            hideLoadingIndicator()
                            showError(productsResult.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun productTypeFiltering(currencyResponse: CurrencyResponse, conversionRate: Double) {
        menuItems = arrayOf(
            binding.shirtsType,
            binding.shoesType,
            binding.accessoriesType,
            binding.allProducts
        )
        binding.filteringOptions.setOnClickListener {
            if (isOpen) {
                closeMenuItems()
            } else {
                showMenuItems()
            }
            isOpen = !isOpen
        }
        binding.shirtsType.setOnClickListener {
            selectedProductType = "T-SHIRTS"
            showProductsByType(currencyResponse, conversionRate)
        }
        binding.shoesType.setOnClickListener {
            selectedProductType = "SHOES"
            showProductsByType(currencyResponse, conversionRate)
        }
        binding.accessoriesType.setOnClickListener {
            selectedProductType = "ACCESSORIES"
            showProductsByType(currencyResponse, conversionRate)
        }
        binding.allProducts.setOnClickListener {
            selectedProductType = null
            showAllProducts(currencyResponse, conversionRate)
        }
    }

    private fun showAllProducts(currencyResponse: CurrencyResponse, conversionRate: Double) {
        setupCategoriesProductsRecyclerview(allProducts, currencyResponse, conversionRate)
        resetPriceFilter(currencyResponse, conversionRate)
    }


    private fun resetPriceFilter(currencyResponse: CurrencyResponse, conversionRate: Double) {
        binding.seekBar.progress = 0
        binding.priceTv.text = "0"
        val filteredProducts = if (selectedProductType != null) {
            allProducts.filter { product -> product.product_type == selectedProductType }
        } else {
            allProducts
        }
        setupCategoriesProductsRecyclerview(filteredProducts, currencyResponse, conversionRate)
    }

    private fun showProductsByType(currencyResponse: CurrencyResponse, conversionRate: Double) {
        val filteredProducts = allProducts.filter { product ->
            product.product_type == selectedProductType
        }
        setupCategoriesProductsRecyclerview(filteredProducts, currencyResponse, conversionRate)
        resetPriceFilter(currencyResponse, conversionRate)
    }

    private fun priceFiltering(currencyResponse: CurrencyResponse, conversionRate: Double) {
        binding.filterPrice.setOnClickListener {
            binding.seekBar.visibility = View.VISIBLE
            binding.priceTv.visibility = View.VISIBLE
            binding.priceTextTv.visibility = View.VISIBLE
        }
        binding.seekBar.progress = 0
        binding.priceTv.text = "0"

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.priceTv.text ="${progress.toDouble()} $selectedCurrency"
                filterProductsByTypeAndPrice(currencyResponse, conversionRate)
                currentSeekBarProgress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar?.progress==0){
                    showAllProducts(currencyResponse,conversionRate)
                }
            }
        })
    }

    private fun filterProductsByTypeAndPrice(
        currencyResponse: CurrencyResponse,
        conversionRate: Double
    ) {
        val filteredProducts = allProducts.filter { product ->
            val matchesType = selectedProductType?.let { product.product_type == it } ?: true

            // Convert the original price to the selected currency
            val originalPrice = product.variants[0].price.toDouble()
            val convertedPrice = originalPrice * conversionRate

            // Compare the converted price to the current seek bar progress
            val matchesPrice = convertedPrice <= currentSeekBarProgress

            // Return true only if both type and price match
            matchesType && matchesPrice
        }
        setupCategoriesProductsRecyclerview(filteredProducts, currencyResponse, conversionRate)
    }



//    private fun filterProductsByPrice(maxPrice: Int) {
//        val filteredProducts = allProducts.filter {
//            it.variants[0].price.toDouble() <= maxPrice
//        }
//        setupCategoriesProductsRecyclerview(filteredProducts)
//    }


    private fun showLoadingIndicator() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.groupLayout.visibility = View.GONE
    }

    private fun hideLoadingIndicator() {
        binding.loadingIndicator.visibility = View.GONE
        binding.groupLayout.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupCategoriesRecyclerview(collection: List<CustomCollection>) {
        val categoriesAdapter = CategoriesAdapter(collection, this)
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.categoriesRv.apply {
            adapter = categoriesAdapter
            layoutManager = manager
        }
        categoriesAdapter.notifyDataSetChanged()
    }

    private fun setupCategoriesProductsRecyclerview(
        product: List<Product>,
        currencyResponse: CurrencyResponse,
        conversionRate: Double
    ) {
        val categoriesProductsAdapter =
            CategoriesProductsAdapter(product, requireContext(), { selectedProduct ->
                val action =
                    CategoriesFragmentDirections.actionCategoriesFragmentToProductDetailsFragment(
                        selectedProduct.id
                    )
                findNavController().navigate(action)
            }, onFavouriteClick = { product, isFavorite ->
                GuestUtil.handleFavoriteClick(requireContext(), sharedPreferences, product)
                val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
                if (shopifyCustomerId != null) {
                    if (isFavorite) {
                        categoriesViewModel.addProductToFavourite(product, shopifyCustomerId)
                        Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        categoriesViewModel.deleteProductFromFavourite(product, shopifyCustomerId)
                        Toast.makeText(
                            requireContext(),
                            "Removed from favorites",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    LocalDataSourceImpl.setProductFavoriteStatus(
                        requireContext(),
                        product.id.toString(),
                        isFavorite
                    )
                }
            }, sharedPreferences, currencyResponse, selectedCurrency, conversionRate)
        val manager = GridLayoutManager(requireContext(), 2)

        binding.productsRv.apply {
            adapter = categoriesProductsAdapter
            layoutManager = manager
        }
        categoriesProductsAdapter.notifyDataSetChanged()
    }

    override fun onCategoryClick(categoryId: Long) {
        collectionId = categoryId
        categoriesViewModel.getProductsOfSelectedCategory(categoryId)
        binding.seekBar.progress = 0
        binding.priceTv.text = "0"

    }


    private fun showMenuItems() {
        for (i in menuItems.indices) {
            val item = menuItems[i]
            item.visibility = View.VISIBLE
            val animator = ObjectAnimator.ofFloat(item, "translationY", -(100 * (i + 1)).toFloat())
            animator.setDuration(400)
            animator.start()
        }
        binding.filteringOptions.setImageResource(R.drawable.ic_close)
        val filteringOptionsFAB = binding.filteringOptions
        filteringOptionsFAB.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )

    }


    private fun closeMenuItems() {
        for (i in menuItems.indices) {
            val item = menuItems[i]
            val animator = ObjectAnimator.ofFloat(item, "translationY", (100 * (i + 2)).toFloat())
            animator.setDuration(400)
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    item.visibility = View.GONE
                }
            })
            animator.start()
        }
        binding.filteringOptions.setImageResource(R.drawable.ic_filterr)

        val filteringOptionsFAB = binding.filteringOptions
        filteringOptionsFAB.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )

    }

    override fun onPause() {
        super.onPause()
        binding.suggestionsRv.visibility = View.GONE

    }

    override fun onResume() {
        super.onResume()
        binding.suggestionsRv.visibility = View.GONE
        binding.edSearch.text?.clear()
        selectedCurrency = LocalDataSourceImpl.getCurrencyText(requireContext())
        LocalDataSourceImpl.saveCurrencyText(requireContext(), selectedCurrency)
    }
}