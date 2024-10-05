package com.example.e_commerce_app.brand_products.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.e_commerce_app.home.view.adapter.RandomProductsAdapter
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.launch

class BrandProductsFragment : Fragment() {
    private lateinit var binding: FragmentBrandProductsBinding
    private lateinit var brandProductViewModel: BrandProductViewModel
    private lateinit var brandName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource)
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
                                hideLoadingIndicator()
                                setupBrandProductsRecyclerview(product.products)
                            }
                        }

                        is ApiState.Error -> {
                            hideLoadingIndicator()
                            //showError(result.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun showLoadingIndicator() {
//        binding.loadingIndicator.visibility = View.VISIBLE
//        binding.groupLayout.visibility = View.GONE
    }

    private fun hideLoadingIndicator() {
//        binding.loadingIndicator.visibility = View.GONE
//        binding.groupLayout.visibility = View.VISIBLE
    }

    private fun setupBrandProductsRecyclerview(product: List<Product>) {
        val brandProductsAdapter =
            BrandProductsAdapter(product, requireContext()) { selectedProduct ->
                val action =
                    BrandProductsFragmentDirections.actionBrandProductsFragmentToProductDetailsFragment(
                    selectedProduct.id
                    )
                findNavController().navigate(action)
            }
        val manager = GridLayoutManager(requireContext(),2)

        binding.productBrandRv.apply {
            adapter = brandProductsAdapter
            layoutManager = manager
        }
    }
}