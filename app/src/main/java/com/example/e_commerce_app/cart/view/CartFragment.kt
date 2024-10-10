package com.example.e_commerce_app.cart.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commerce_app.cart.DraftOrderManager
import com.example.e_commerce_app.cart.adapter.CartAdapter
import com.example.e_commerce_app.cart.viewmodel.CartViewModel
import com.example.e_commerce_app.cart.viewmodel.CartViewModelFactory
import com.example.e_commerce_app.databinding.FragmentCartBinding
import com.example.e_commerce_app.db.SharedPrefsManager
import com.example.e_commerce_app.db.ShopifyDB
import com.example.e_commerce_app.model.cart.Cart
import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.DraftOrderResponse
import com.example.e_commerce_app.model.cart.LineItem
import com.example.e_commerce_app.model.repo.ShopifyRepoImpl
import com.example.e_commerce_app.network.RemoteDataSourceImpl
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var viewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter
    //private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val shopifyDao = ShopifyDB.getInstance(requireContext()).shopifyDao()
        val repo = ShopifyRepoImpl(RemoteDataSourceImpl())
        val factory = CartViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[CartViewModel::class.java]
        val draftOrderId = SharedPrefsManager.getInstance().getDraftedOrderId() ?: 0
        viewModel.getProductsFromDraftOrder(draftOrderId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGetDraftOrderData()
        // Set up RecyclerView
        setupRecyclerView()

        // Set up checkout button click listener
        binding.checkoutBtn.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToAddressFragment("cart")
            findNavController().navigate(action)
            //viewModel.completeOrderForSultan()
            //observeCompleteOrder()
        }

        // Fetch products based on customer ID
        //fetchProductsFromDraftOrder()
    }

    private fun observeGetDraftOrderData() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.draftOrderState.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {
                        }

                        is ApiState.Success -> {
                            result.data?.let { collections ->
                                updateCartUI(collections)
                                /*var lineItemsList = mutableListOf<LineItems>()
                                for(lineItem in collections.draft_order.line_items){
                                    lineItemsList.add(
                                        LineItems(
                                            title = lineItem.title,
                                            price = lineItem.price,
                                            quantity = lineItem.quantity,
                                            productId = lineItem.productId ?: "null",
                                            variantId = lineItem.variantId
                                        )
                                    )
                                }
                                DraftOrderManager.init(
                                    DraftOrder(
                                    lineItems = lineItemsList,
                                    customer = CustomerId(collections.draft_order.customer.id)
                                )
                                )*/
                            }
                        }

                        is ApiState.Error -> {
                            Log.e("TAG", "observeDraftOrderId: ${result.message}", )
                            showError(result.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            lineItems = emptyList(), // Initially empty
            onDeleteClick = { lineItem -> handleDeleteClick(lineItem) },
            onIncreaseQuantity = { lineItem -> handleIncreaseQuantity(lineItem) },
            onDecreaseQuantity = { lineItem -> handleDecreaseQuantity(lineItem) }
        )

        binding.cartProductsRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }
/*
    private fun fetchProductsFromDraftOrder() {
        //val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)

        Log.d("CartFragment", "shopifyCustomerId: $shopifyCustomerId")

        // Check if shopifyCustomerId is null
        if (shopifyCustomerId == null) {
            Log.e("TAG", "No Shopify customer ID found in SharedPreferences.")
            showError("No Shopify customer ID found in SharedPreferences.")
            return
        }

        // Get the draft order ID associated with the customer
        val draftFavoriteId = getDraftOrderIdForCustomer(shopifyCustomerId)

        // Check if draftFavoriteId is null
        if (draftFavoriteId == null) {
            Log.e("CartFragment", "No draft order ID found for the customer.")
            showNoDraftOrderMessage()
            return
        }

        // Fetch products from the draft order
        viewModel.getProductsFromDraftOrder(draftFavoriteId)

        lifecycleScope.launch {
            viewModel.draftOrderState.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        // Optionally show loading indicator
                        Log.d("CartFragment", "Loading draft order products...")
                    }
                    is ApiState.Success -> {
                        val draftOrderResponse = state.data as DraftOrderResponse
                        val cartResponse = createCartResponseFromDraftOrder(draftOrderResponse)
                        updateCartUI(cartResponse)
                    }
                    is ApiState.Error -> {
                        Log.e("CartFragment", "Error fetching products: ${state.message}")
                        showError(state.message)
                    }
                }
            }
        }
    }
    */

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error fetching products: ${message ?: "Unknown error occurred."}", Toast.LENGTH_LONG).show()
    }

    private fun showNoDraftOrderMessage() {
        Toast.makeText(requireContext(), "No draft order found for your account.", Toast.LENGTH_LONG).show()
    }

    private fun updateCartUI(draftOrderResponse: DraftOrderResponse) {
        val lineItems = draftOrderResponse.draft_order.line_items
        val imageUrls = DraftOrderManager.getInstance().draftOrder.note
        val result = imageUrls.split("|##|").filter { it.isNotEmpty() }
        for ((index, item) in lineItems.drop(1).withIndex()) {
            if (index < result.size) {
                item.imageUrl = result[index] // Assign corresponding imageUrl
            }
        }
        cartAdapter.updateData(lineItems.drop(1))
        binding.totalPrice.text = draftOrderResponse.draft_order.subtotal_price
        /*if (cartResponse.carts.isNotEmpty()) {
            val lineItems = cartResponse.carts[0].lineItems
            cartAdapter.updateData(lineItems)
        } else {
            cartAdapter.updateData(emptyList())
        }*/
    }

    private fun getDraftOrderIdForCustomer(customerId: String?): Long? {
        return customerId?.toLong()
    }

    private fun handleDeleteClick(lineItem: LineItem) {
        Log.d("CartFragment", "Delete item: ${lineItem.title}")
        // Show delete confirmation dialog
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete ${lineItem.title}?")

        // Set the positive button (Yes)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            Log.d("CartFragment", "Deleted item: ${lineItem.title}")
            SharedPrefsManager.getInstance().getDraftedOrderId()?.let {
                viewModel.UpdateDraftOrderProducts(DraftOrderRequest(DraftOrderManager.getInstance().delete(lineItem)),
                    it
                )
            }
            dialogInterface.dismiss()  // Close the dialog
        }

        // Set the negative button (No)
        builder.setNegativeButton("No") { dialogInterface, _ ->
            // Just dismiss the dialog if "No" is clicked
            dialogInterface.dismiss()
        }

        // Create and show the dialog
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun handleIncreaseQuantity(lineItem: LineItem) {
        viewModel.UpdateDraftOrderProducts(DraftOrderRequest(
            DraftOrderManager.getInstance().IncreaseQuantity(lineItem)
        ),
            SharedPrefsManager.getInstance().getDraftedOrderId() ?: 0
        )
    }

    private fun handleDecreaseQuantity(lineItem: LineItem) {
        if (lineItem.quantity == 1){
            handleDeleteClick(lineItem)
        }else{
            viewModel.UpdateDraftOrderProducts(DraftOrderRequest(
                DraftOrderManager.getInstance().DecreaseQuantity(lineItem)
            ),
                SharedPrefsManager.getInstance().getDraftedOrderId() ?: 0
            )
        }

    }

    private fun createCartResponseFromDraftOrder(draftOrderResponse: DraftOrderResponse): CartResponse {
        val lineItems = draftOrderResponse.draft_order.line_items.map { lineItem ->
            LineItem(
                id = lineItem.id.toString(),
                productId = lineItem.productId.toString(),
                quantity = lineItem.quantity,
                title = lineItem.title,
                price = lineItem.price,
                totalPrice = (lineItem.price.toDouble() * lineItem.quantity).toString(),
                imageUrl = lineItem.imageUrl
            )
        }

        return CartResponse(carts = listOf(Cart(id = draftOrderResponse.draft_order.id.toString(), lineItems = lineItems)))
    }
}
