package com.example.e_commerce_app.categories.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_app.categories.CategoriesAdapter
import com.example.e_commerce_app.categories.viewmodel.CategoriesViewModel
import com.example.e_commerce_app.categories.viewmodel.CategoriesViewModelFactory
import com.example.e_commerce_app.databinding.FragmentCategoriesBinding
import com.example.e_commerce_app.model.custom_collection.CustomCollection
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class CategoriesFragment : Fragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoriesViewModel: CategoriesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val remoteDataSource = RemoteDataSourceImpl()
        val repo = ShopifyRepoImpl(remoteDataSource)
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
        categoriesViewModel.getCategorise()
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
    }

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupCategoriesRecyclerview(collection: List<CustomCollection>) {
        val categoriesAdapter = CategoriesAdapter(collection)
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL
        binding.categoriesRv.apply {
            adapter = categoriesAdapter
            layoutManager = manager
        }
    }
}