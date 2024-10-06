package com.example.e_commerce_app.home.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce_app.databinding.FragmentHomeBinding
import com.example.e_commerce_app.home.view.adapter.BrandsAdapter
import com.example.e_commerce_app.home.view.adapter.RandomProductsAdapter
import com.example.e_commerce_app.home.view.adapter.SuggestionsAdapter
import com.example.e_commerce_app.home.viewmodel.HomeViewModel
import com.example.e_commerce_app.home.viewmodel.HomeViewModelFactory
import com.example.e_commerce_app.model.coupon.Discount
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.smart_collection.SmartCollection
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var discountPagerAdapter: DiscountPagerAdapter

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


        searchSetUp()


        // Find views
        viewPager = binding.couponPager
        dotsIndicator = binding.dotsIndicator

        // Sample data
        val discounts = listOf(
            Discount(
                "https://img.freepik.com/premium-vector/trendy-flat-advertising-with-25-percent-discount-flat-badge-promo-design-poster-badge_123447-1341.jpg",
                "XKK1CF4D5QHW"
            ),
            Discount(
                "https://t4.ftcdn.net/jpg/06/16/99/51/360_F_616995128_xWAth0i92Adkm4A9b6RGXCnO5yocUmnU.jpg",
                "A7GQS8QFAT1Z"
            ),
            Discount(
                "https://static.vecteezy.com/system/resources/thumbnails/013/549/310/small/15-percent-off-special-discount-offer-15-off-sale-of-advertising-campaign-graphics-free-vector.jpg",
                "9GZABPQP6CGZ"
            ),
            Discount(
                "https://media.istockphoto.com/id/1490275605/vector/hand-drawn-style-10-percent-off-discount-sale-promotion-label-illustration-vector.jpg?s=612x612&w=0&k=20&c=GAoOKGrDaIO-6fZzzTchg5tM5wJKINt5igU34ipbDYs=",
                "SATR3GWQR9PT"
            ),
            Discount(
                "https://drive.google.com/uc?export=download&id=1zAUMAjtzIXoF475iIHL8zEdfyY9LuyXh",
                "4X5JCEG8FTD0"
            ),
        )

        // Set up ViewPager2 adapter
        discountPagerAdapter = DiscountPagerAdapter(discounts)
        viewPager.adapter = discountPagerAdapter

        // Attach DotsIndicator to ViewPager2
        dotsIndicator.setViewPager2(viewPager)

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



    //search
    private fun searchSetUp() {
        binding.suggestionsRv.visibility = View.GONE

        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                homeViewModel.filterProducts(searchText)

                binding.suggestionsRv.visibility = if (searchText.isNotEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.filteredProducts.collect { filteredList ->
                    setupSuggestionsRecyclerview(filteredList)
                    setupRandomProductsRecyclerview(filteredList.ifEmpty { homeViewModel.originalProducts })
                    if (filteredList.isEmpty() && binding.edSearch.text.isNotEmpty()) {
                        binding.suggestionsRv.visibility = View.GONE
                    }
                }
            }
        }
    }


    private fun setupSuggestionsRecyclerview(filteredList: List<Product>) {
        val suggestions = filteredList.map { it.title }

        if (suggestions.isNotEmpty()) {
            binding.suggestionsRv.visibility = View.VISIBLE
            val suggestionsAdapter = SuggestionsAdapter(suggestions) { selectedSuggestion ->
                binding.edSearch.setText(selectedSuggestion)
                binding.suggestionsRv.visibility = View.GONE
            }
            binding.suggestionsRv.adapter = suggestionsAdapter
            binding.suggestionsRv.layoutManager = LinearLayoutManager(requireContext())
        } else {
            binding.suggestionsRv.visibility = View.GONE
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
        binding.dotsIndicator.visibility = View.VISIBLE
        binding.couponPager.visibility = View.VISIBLE
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