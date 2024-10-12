package com.example.e_commerce_app.brand_products.view

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.R
import com.example.e_commerce_app.brand_products.viewmodel.BrandProductViewModel
import com.example.e_commerce_app.brand_products.viewmodel.BrandProductViewModelFactory
import com.example.e_commerce_app.databinding.FragmentBrandProductsBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.home.view.adapter.SuggestionsAdapter
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.currencyResponse.Rates
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState
import com.example.e_commerce_app.util.GuestUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class BrandProductsFragment : Fragment() {
    private lateinit var binding: FragmentBrandProductsBinding
    private lateinit var brandProductViewModel: BrandProductViewModel
    private lateinit var brandName: String
    private var allProducts: List<Product> = emptyList()
    private var filteredProducts: List<Product> = emptyList()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var suggestionsAdapter: SuggestionsAdapter
    private lateinit var selectedCurrency: String


    //    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var linearLayoutManager: LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = ShopifyDB.getInstance(requireContext()).shopifyDao()
        val localDataSource = LocalDataSourceImpl(dao)
        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource, localDataSource)
        val factory = BrandProductViewModelFactory(repo)
        brandProductViewModel = ViewModelProvider(this, factory)[BrandProductViewModel::class.java]
        brandName = BrandProductsFragmentArgs.fromBundle(requireArguments()).brandName
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBrandProductsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
        brandProductViewModel.fetchCurrencyRates()

        linearLayoutManager = LinearLayoutManager(requireContext())

        suggestionsAdapter = SuggestionsAdapter(emptyList()) { selectedProduct ->
            val action =
                BrandProductsFragmentDirections.actionBrandProductsFragmentToProductDetailsFragment(
                    selectedProduct.id
                )
            findNavController().navigate(action)
        }

        binding.suggestionsRv.adapter = suggestionsAdapter
        binding.suggestionsRv.layoutManager = linearLayoutManager

        searchSetUp()

        binding.filterPrice.setOnClickListener {
            binding.seekBar.visibility = View.VISIBLE
            binding.priceTv.visibility = View.VISIBLE
            binding.priceTextTv.visibility = View.VISIBLE
        }
        brandProductViewModel.getBrandProducts(brandName)
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                brandProductViewModel.brandProductResult.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            showLoadingIndicator()
                        }

                        is ApiState.Success -> {
                            hideLoadingIndicator()
                            result.data?.let { product ->
                               val  productPrice=product.products[0].variants[0].price.toDouble()
                                brandProductViewModel.currencyRates.collect {
                                    val currencyResponse = it.data ?: CurrencyResponse(
                                        "",
                                        "",
                                        Rates(productPrice, productPrice, productPrice),
                                        true,
                                        0
                                    )

                                    val conversionRate = when (selectedCurrency) {
                                        "USD" -> currencyResponse.rates.USD
                                        "EUR" -> currencyResponse.rates.EUR
                                        "EGP" -> currencyResponse.rates.EGP
                                        else -> 0.0
                                    }
                                    allProducts = product.products
                                    brandProductViewModel.setAllProducts(allProducts)
                                    setupBrandProductsRecyclerview(
                                        allProducts,
                                        currencyResponse,
                                        conversionRate
                                    )
                                    setupSeekBar(currencyResponse, conversionRate)
                                }
                            }
                        }

                        is ApiState.Error -> {
                            hideLoadingIndicator()
                            showError(result.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun setupSeekBar(currencyResponse: CurrencyResponse, conversionRate: Double) {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                filterProductsByPrice(progress, currencyResponse, conversionRate)
                binding.priceTv.text = "${progress.toDouble()} $selectedCurrency"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar?.progress==0){
                    setupBrandProductsRecyclerview(allProducts,currencyResponse,conversionRate)
                }
            }
        })

    }

    private fun filterProductsByPrice(
        maxPrice: Int,
        currencyResponse: CurrencyResponse,
        conversionRate: Double
    ) {

        filteredProducts = allProducts.filter {
            val price = it.variants[0].price.toDouble()
            val convertedPrice = conversionRate * price
            convertedPrice <= maxPrice
        }

        setupBrandProductsRecyclerview(filteredProducts, currencyResponse, conversionRate)
    }

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

    private fun setupBrandProductsRecyclerview(
        product: List<Product>,
        currencyResponse: CurrencyResponse,
        conversionRate: Double
    ) {
        val brandProductsAdapter =
            BrandProductsAdapter(product, requireContext(), { selectedProduct ->
                val action =
                    BrandProductsFragmentDirections.actionBrandProductsFragmentToProductDetailsFragment(
                        selectedProduct.id
                    )
                findNavController().navigate(action)
            }, onFavouriteClick = { product, isFavorite ->
                GuestUtil.handleFavoriteClick(requireContext(), sharedPreferences, product)
                val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
                if (shopifyCustomerId != null) {
                    if (isFavorite) {
                        brandProductViewModel.addProductToFavourite(
                            product,
                            shopifyCustomerId
                        )
                        Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        brandProductViewModel.deleteProductFromFavourite(
                            product,
                            shopifyCustomerId
                        )
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

        binding.productBrandRv.apply {
            adapter = brandProductsAdapter
            layoutManager = manager
        }
    }


    private fun searchSetUp() {
        binding.suggestionsRv.visibility = View.GONE

        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                filterProducts(searchText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun filterProducts(searchText: String) {
        lifecycleScope.launch {
            filteredProducts = if (searchText.isEmpty()) {
                allProducts
            } else {
                allProducts.filter { product ->
                    product.title.contains(searchText, ignoreCase = true)
                }
            }

            suggestionsAdapter.updateProducts(filteredProducts)
            val currencyResponse = CurrencyResponse("", "", Rates(0.0, 0.0, 0.0), true, 0)
            setupBrandProductsRecyclerview(filteredProducts, currencyResponse, 0.0)
            binding.suggestionsRv.visibility =
                if (searchText.isNotEmpty() && filteredProducts.isNotEmpty()) View.VISIBLE else View.GONE
        }
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


    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE

    }
}