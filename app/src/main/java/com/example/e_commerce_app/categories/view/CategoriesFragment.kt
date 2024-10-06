package com.example.e_commerce_app.categories.view

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
import com.example.e_commerce_app.categories.view.adapter.CategoriesAdapter
import com.example.e_commerce_app.categories.view.adapter.CategoriesProductsAdapter
import com.example.e_commerce_app.categories.view.adapter.OnCategoryClick
import com.example.e_commerce_app.categories.viewmodel.CategoriesViewModel
import com.example.e_commerce_app.categories.viewmodel.CategoriesViewModelFactory
import com.example.e_commerce_app.databinding.FragmentCategoriesBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.model.custom_collection.CustomCollection
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class CategoriesFragment : Fragment(), OnCategoryClick {
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoriesViewModel: CategoriesViewModel
    private var allProducts: List<Product> = emptyList()
    private var filteredProducts: List<Product> = emptyList()
    private var currentSeekBarProgress: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao=ShopifyDB.getInstance(requireContext()).shopifyDao()
        val localDataSource=LocalDataSourceImpl(dao)
        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource,localDataSource)
        val factory = CategoriesViewModelFactory(repo)
        categoriesViewModel = ViewModelProvider(this, factory)[CategoriesViewModel::class.java]
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

        binding.filterPrice.setOnClickListener {
            binding.seekBar.visibility = View.VISIBLE
            binding.priceTv.visibility = View.VISIBLE
            binding.priceTextTv.visibility = View.VISIBLE
        }
        binding.priceTv.text = "0"
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.priceTv.text = progress.toString()
                currentSeekBarProgress = progress
                filterProductsByPrice(currentSeekBarProgress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        categoriesViewModel.getCategorise()
        categoriesViewModel.getProductsOfSelectedCategory(482200682811)
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                categoriesViewModel.categoriesResult.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {

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
                categoriesViewModel.productsResult.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            showLoadingIndicator()
                        }

                        is ApiState.Success -> {
                            hideLoadingIndicator()
                            allProducts = result.data?.products ?: emptyList()
                            setupCategoriesProductsRecyclerview(allProducts)
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
        setupCategoriesProductsRecyclerview(filteredProducts)
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

    private fun setupCategoriesRecyclerview(collection: List<CustomCollection>) {
        val categoriesAdapter = CategoriesAdapter(collection, this)
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.categoriesRv.apply {
            adapter = categoriesAdapter
            layoutManager = manager
        }
    }

    private fun setupCategoriesProductsRecyclerview(product: List<Product>) {
        val categoriesProductsAdapter =
            CategoriesProductsAdapter(product, requireContext(), { selectedProduct ->
                val action =
                    CategoriesFragmentDirections.actionCategoriesFragmentToProductDetailsFragment(
                        selectedProduct.id
                    )
                findNavController().navigate(action)
            }, onFavouriteClick = { product, isFavorite ->
                if (isFavorite) {
                    categoriesViewModel.addProductToFavourite(product)
                    Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    categoriesViewModel.deleteProductToFavourite(product)
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

        binding.productsRv.apply {
            adapter = categoriesProductsAdapter
            layoutManager = manager
        }
    }

    override fun onCategoryClick(categoryId: Long) {
        categoriesViewModel.getProductsOfSelectedCategory(categoryId)
        binding.seekBar.progress = 0
        binding.priceTv.text = "0"

    }
}