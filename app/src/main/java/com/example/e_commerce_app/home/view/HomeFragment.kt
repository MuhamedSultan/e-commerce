package com.example.e_commerce_app.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.e_commerce_app.databinding.FragmentHomeBinding
import com.example.e_commerce_app.home.viewmodel.HomeViewModel
import com.example.e_commerce_app.home.viewmodel.HomeViewModelFactory
import com.example.e_commerce_app.model.coupon.Discount
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

        // Find views
        viewPager = binding.couponPager
        dotsIndicator = binding.dotsIndicator

        // Sample data
        val discounts = listOf(
            Discount("https://img.freepik.com/premium-vector/trendy-flat-advertising-with-25-percent-discount-flat-badge-promo-design-poster-badge_123447-1341.jpg", "XKK1CF4D5QHW"),
            Discount("https://t4.ftcdn.net/jpg/06/16/99/51/360_F_616995128_xWAth0i92Adkm4A9b6RGXCnO5yocUmnU.jpg", "A7GQS8QFAT1Z"),
            Discount("https://static.vecteezy.com/system/resources/thumbnails/013/549/310/small/15-percent-off-special-discount-offer-15-off-sale-of-advertising-campaign-graphics-free-vector.jpg", "9GZABPQP6CGZ"),
            Discount("https://media.istockphoto.com/id/1490275605/vector/hand-drawn-style-10-percent-off-discount-sale-promotion-label-illustration-vector.jpg?s=612x612&w=0&k=20&c=GAoOKGrDaIO-6fZzzTchg5tM5wJKINt5igU34ipbDYs=", "SATR3GWQR9PT"),
            Discount("https://drive.google.com/uc?export=download&id=1zAUMAjtzIXoF475iIHL8zEdfyY9LuyXh", "4X5JCEG8FTD0"),
        )

        // Set up ViewPager2 adapter
        discountPagerAdapter = DiscountPagerAdapter(discounts)
        viewPager.adapter = discountPagerAdapter

        // Attach DotsIndicator to ViewPager2
        dotsIndicator.setViewPager2(viewPager)

        homeViewModel.getAllBrands()
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
                                setupRecyclerview(collections.smart_collections)
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
        binding.dotsIndicator.visibility = View.VISIBLE
        binding.couponPager.visibility = View.VISIBLE
        binding.groupLayout.visibility = View.VISIBLE
    }

    private fun setupRecyclerview(smartCollection: List<SmartCollection>) {
        val brandAdapter = BrandsAdapter(smartCollection, requireContext())
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.brandRv.apply {
            adapter = brandAdapter
            layoutManager = manager
        }
    }

}