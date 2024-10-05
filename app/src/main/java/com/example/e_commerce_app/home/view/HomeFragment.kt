package com.example.e_commerce_app.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_app.databinding.FragmentHomeBinding
import com.example.e_commerce_app.home.view.adapter.BrandsAdapter
import com.example.e_commerce_app.home.view.adapter.RandomProductsAdapter
import com.example.e_commerce_app.home.viewmodel.HomeViewModel
import com.example.e_commerce_app.home.viewmodel.HomeViewModelFactory
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.smart_collection.SmartCollection
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource)
        val factory = HomeViewModelFactory(repo)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToFavoriteFragment()
            findNavController().navigate(action)
        }


        homeViewModel.getAllBrands()
        homeViewModel.getRandomProducts()
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.brandsResult.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            showLoadingIndicator()
                        }

                        is ApiState.Success -> {
                            hideLoadingIndicator()
                            result.data?.let { collections ->
                                hideLoadingIndicator()
                                setupBrandRecyclerview(collections.smart_collections)
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
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                homeViewModel.randProductsResult.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                            showLoadingIndicator()
                        }

                        is ApiState.Success -> {
                            hideLoadingIndicator()
                            result.data?.let { product ->
                                hideLoadingIndicator()
                                val randomProducts = product.products.shuffled().take(15)
                                setupRandomProductsRecyclerview(randomProducts)
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

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoadingIndicator() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.groupLayout.visibility = View.GONE
    }

    private fun hideLoadingIndicator() {
        binding.loadingIndicator.visibility = View.GONE
        binding.groupLayout.visibility = View.VISIBLE
    }

    private fun setupBrandRecyclerview(smartCollection: List<SmartCollection>) {
        val brandAdapter = BrandsAdapter(smartCollection, requireContext()) { selectedBrand ->
            val action =
                HomeFragmentDirections.actionHomeFragmentToBrandProductsFragment(selectedBrand.title)
            findNavController().navigate(action)
        }
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.brandRv.apply {
            adapter = brandAdapter
            layoutManager = manager
        }
    }

    private fun setupRandomProductsRecyclerview(product: List<Product>) {
        val randomProductsAdapter =
            RandomProductsAdapter(product, requireContext()) { selectedProduct ->
                val action = HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(
                    selectedProduct.id
                )
                findNavController().navigate(action)
            }
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL

        binding.recommendedProductsRv.apply {
            adapter = randomProductsAdapter
            layoutManager = manager
        }
    }
}