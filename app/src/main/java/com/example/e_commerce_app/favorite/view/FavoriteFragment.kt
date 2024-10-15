package com.example.e_commerce_app.favorite.view


import FavoriteViewModel
import FavoriteViewModelFactory
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.e_commerce_app.MainActivity
import com.example.e_commerce_app.R
import com.example.e_commerce_app.databinding.FragmentFavoriteBinding
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.favorite.adapter.FavoriteAdapter
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest

class FavoriteFragment : Fragment() {
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var shopifyCustomerId: String? = null
    private lateinit var lottieView: LottieAnimationView
    private lateinit var binding: FragmentFavoriteBinding
    private val favoriteViewModel: FavoriteViewModel by viewModels {
        FavoriteViewModelFactory(
            ShopifyRepoImpl(
                RemoteDataSourceImpl(),
                LocalDataSourceImpl(ShopifyDB.getInstance(requireContext()).shopifyDao())
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE

        setupRecyclerView()
        observeFavorites()
        lottieView = binding.lottieView

        shopifyCustomerId?.let { id ->
            favoriteViewModel.getAllFavorites(id)
        }

        return binding.root
    }

    private fun observeFavorites() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            favoriteViewModel.favorites.collectLatest { favoriteProducts ->

                favoriteAdapter.submitList(favoriteProducts)
                if (shopifyCustomerId== null) {
                    binding.textView.visibility = View.VISIBLE
                    binding.textView.text = "You are a guest,Please log in to proceed!"
                }
                if (favoriteProducts.isEmpty()) {
                    binding.recyclerView.visibility =
                        View.GONE
                    lottieView.visibility = View.VISIBLE
                    lottieView.playAnimation()
                    binding.textView.visibility = View.VISIBLE
                } else {
                    binding.recyclerView.visibility =
                        View.VISIBLE
                    lottieView.visibility = View.GONE
                    binding.textView.visibility = View.GONE
                }
            }
        }
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter(
            onDeleteClick = { product ->
                shopifyCustomerId?.let { id ->
                    favoriteViewModel.removeFavorite(product, id)
                    LocalDataSourceImpl.setProductFavoriteStatus(
                        requireContext(),
                        product.id.toString(),
                        false
                    )
                }
            },
            onProductClick = { productId ->
                val action = FavoriteFragmentDirections
                    .actionFavoriteFragmentToProductDetailsFragment(productId)
                findNavController().navigate(action)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
    }
}
