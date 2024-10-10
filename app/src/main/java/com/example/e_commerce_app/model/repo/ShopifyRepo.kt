package com.example.e_commerce_app.model.repo

import com.example.e_commerce_app.model.address.AddressRequest
import com.example.e_commerce_app.model.address.AddressResponse
import com.example.e_commerce_app.model.address.AddressesResponse
import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.cart.DeleteProductResponse
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.DraftOrderResponse
import com.example.e_commerce_app.model.cart.PriceRuleResponse
import com.example.e_commerce_app.model.cart.UpdateCartItemRequest
import com.example.e_commerce_app.model.cart.UpdateCartItemResponse
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.custom_collection.CustomCollectionResponse
import com.example.e_commerce_app.model.order_details.OrderDetailsResponse
import com.example.e_commerce_app.model.orders.CustomerOrders
import com.example.e_commerce_app.model.orders.Order
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.util.ApiState

interface ShopifyRepo {
    suspend fun getAllBrands(): ApiState<SmartCollectionResponse>
    suspend fun getRandomProducts(): ApiState<ProductResponse>
    suspend fun getBrandProducts(brandName: String): ApiState<ProductResponse>
    suspend fun signInUser(email: String, password: String): ApiState<UserData>
    suspend fun registerUser(userData: UserData): ApiState<Unit>
    suspend fun createShopifyCustomer(customerRequest: CustomerRequest): ApiState<String>
    suspend fun getProductById(productId: Long): ApiState<Product>
    suspend fun getCategories(): ApiState<CustomCollectionResponse>
    suspend fun getProductsOfSelectedCategory(collectionId: Long): ApiState<ProductResponse>


    suspend fun addToFavorite(product: Product)

    //    suspend fun getAllFavorites(): List<Product>
    suspend fun removeFavorite(product: Product)

//    fun getAllCartProducts(): Flow<CartResponse>
    //suspend fun getDraftIds(customerId: String): Flow<ApiState<List<String>>>

    suspend fun getAllFavorites(shopifyCustomerId: String): List<Product>
    suspend fun searchProductsByTitle(title: String): ApiState<ProductResponse>

    suspend fun saveShopifyCustomerId(userId: String, shopifyCustomerId: String): ApiState<Unit>
    suspend fun getAllAddresses(customerId: String): ApiState<AddressesResponse>
    suspend fun insertAddress(customerId: Long, addressResponse: AddressRequest): ApiState<AddressResponse>


    suspend fun createFavoriteDraft(draftOrderRequest: DraftOrderRequest): ApiState<DraftOrderResponse>
    suspend fun getProductsIdForDraftFavorite(draftFavoriteId: Long): ApiState<DraftOrderResponse>
    suspend fun addOrderFromDraftOrder(draftFavoriteId: Long , paymentPending : Boolean): ApiState<DraftOrderResponse>
    suspend fun backUpDraftFavorite(draftOrderRequest: DraftOrderRequest, draftFavoriteId: Long): ApiState<DraftOrderResponse>

    suspend fun getCartById(cartId: String): ApiState<CartResponse>
    suspend fun getCustomerOrders(customerId: Long): ApiState<CustomerOrders>
    suspend fun getOrderDetailsByID(orderId: Long): ApiState<OrderDetailsResponse>

    suspend fun getAllCoupons() :ApiState<PriceRuleResponse>
    suspend fun exchangeRate():ApiState<CurrencyResponse>

}

