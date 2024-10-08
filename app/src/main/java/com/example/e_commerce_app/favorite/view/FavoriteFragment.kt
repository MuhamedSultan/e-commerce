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
import com.example.e_commerce_app.R
import com.example.e_commerce_app.db.LocalDataSource
import com.example.e_commerce_app.db.LocalDataSourceImpl
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.favorite.adapter.FavoriteAdapter
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import kotlinx.coroutines.flow.collectLatest

class FavoriteFragment : Fragment() {
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var shopifyCustomerId: String? = null

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
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        setupRecyclerView(view)
        observeFavorites()
        // Retrieve shopifyCustomerId from SharedPreferences
        val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
        if (shopifyCustomerId != null) {
            favoriteViewModel.getAllFavorites(shopifyCustomerId)
        }

        imageView = view.findViewById(R.id.imageView4)
        textView = view.findViewById(R.id.textView)

        return view
    }

    private fun observeFavorites() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            favoriteViewModel.favorites.collectLatest { favoriteProducts ->
                favoriteAdapter.submitList(favoriteProducts)

                if (favoriteProducts.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    textView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                    textView.visibility = View.GONE
                }
            }
        }
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        favoriteAdapter = FavoriteAdapter(
            onDeleteClick = { product ->
                // Pass shopifyCustomerId to removeFavorite only if it is not null
                shopifyCustomerId?.let { id ->
                    favoriteViewModel.removeFavorite(product, id)
                    LocalDataSourceImpl.setMealFavoriteStatus(
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

        recyclerView.apply {

            layoutManager = LinearLayoutManager(context)
            adapter = favoriteAdapter
        }
    }
}