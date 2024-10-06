package com.example.e_commerce_app.brand_products.view

import android.os.Bundle
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
import com.example.e_commerce_app.R
import com.example.e_commerce_app.brand_products.viewmodel.BrandProductViewModel
import com.example.e_commerce_app.brand_products.viewmodel.BrandProductViewModelFactory
import com.example.e_commerce_app.databinding.FragmentBrandProductsBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.home.view.adapter.RandomProductsAdapter
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class BrandProductsFragment : Fragment() {
    private lateinit var binding: FragmentBrandProductsBinding
    private lateinit var brandProductViewModel: BrandProductViewModel
    private lateinit var brandName: String
    private var allProducts: List<Product> = emptyList()
    private var filteredProducts: List<Product> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = ShopifyDB.getInstance(requireContext()).shopifyDao()
        val localDataSource = LocalDataSourceImpl(dao)
        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource, localDataSource)
        val factory = BrandProductViewModelFactory(repo)
        brandProductViewModel = ViewModelProvider(this, factory)[BrandProductViewModel::class.java]
        brandName = BrandProductsFragmentArgs.fromBundle(requireArguments()).brandName
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

        binding.filterPrice.setOnClickListener {
            binding.seekBar.visibility = View.VISIBLE
            binding.priceTv.visibility = View.VISIBLE
            binding.priceTextTv.visibility = View.VISIBLE
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                filterProductsByPrice(progress)
                binding.priceTv.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
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
                                allProducts = product.products
                                setupBrandProductsRecyclerview(allProducts)
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

    private fun filterProductsByPrice(maxPrice: Int) {
        filteredProducts = allProducts.filter {
            it.variants[0].price.toDouble() <= maxPrice
        }
        setupBrandProductsRecyclerview(filteredProducts)
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

    private fun setupBrandProductsRecyclerview(product: List<Product>) {
        val brandProductsAdapter =
            BrandProductsAdapter(product, requireContext(), { selectedProduct ->
                val action =
                    BrandProductsFragmentDirections.actionBrandProductsFragmentToProductDetailsFragment(
                        selectedProduct.id
                    )
                findNavController().navigate(action)
            }, onFavouriteClick = { product, isFavorite ->
                if (isFavorite) {
                    brandProductViewModel.addProductToFavourite(product)
                    Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    brandProductViewModel.deleteProductToFavourite(product)
                    Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                        .show()
                }
                LocalDataSourceImpl.setMealFavoriteStatus(
                    requireContext(),
                    product.id.toString(),
                    isFavorite
                )
            })

        val manager = GridLayoutManager(requireContext(), 2)

        binding.productBrandRv.apply {
            adapter = brandProductsAdapter
            layoutManager = manager
        }
    }
}