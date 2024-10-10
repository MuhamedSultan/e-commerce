package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.address.AddressRequest
import com.example.e_commerce_app.model.address.AddressResponse
import com.example.e_commerce_app.model.address.AddressesResponse
import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.cart.DeleteProductResponse
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.DraftOrderResponse
import com.example.e_commerce_app.model.cart.PriceRuleResponse
import com.example.e_commerce_app.model.custom_collection.CustomCollectionResponse
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.product.SingleProductResponse
import com.example.e_commerce_app.model.cart.UpdateCartItemRequest
import com.example.e_commerce_app.model.cart.UpdateCartItemResponse
import com.example.e_commerce_app.model.order_details.OrderDetailsResponse
import com.example.e_commerce_app.model.orders.CustomerOrders
import com.example.e_commerce_app.model.orders.Order
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.CustomerResponse
import com.example.e_commerce_app.util.ApiState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ShopifyService {

    // POST request to create a customer in Shopify
    @POST("customers.json")
    suspend fun createCustomer(
        @Body customerRequest: CustomerRequest
    ): Response<CustomerResponse>

    @GET("carts.json")
    suspend fun getAllCartProducts(): Response<CartResponse>

    // New method to delete a specific product from a cart
    @DELETE("carts/{cart_id}/line_items/{line_item_id}.json")
    suspend fun deleteCartProduct(
        @Path("cart_id") cartId: String,
        @Path("line_item_id") lineItemId: String
    ): Response<DeleteProductResponse>

    @GET("smart_collections.json")
    suspend fun getAllBrands(): SmartCollectionResponse

    @GET("products.json")
    suspend fun getRandomProducts(): ProductResponse


    @GET("products/{id}.json")
    suspend fun fetchProductById(@Path("id") productId: Long): Response<SingleProductResponse>

    @GET("products.json")
    suspend fun getBrandProducts(@Query("vendor") brandName: String): ProductResponse

    @GET("custom_collections.json")
    suspend fun getCategories(): CustomCollectionResponse

    @GET("products.json")
    suspend fun getProductsOfSelectedCategory(@Query("collection_id") collectionId: Long): ProductResponse


    // Search products by title
    @GET("products.json")
    suspend fun searchProductsByTitle(
        @Query("title") title: String
    ): Response<ProductResponse>

    /// cart apis

    @POST("draft_orders.json")
    suspend fun createFavoriteDraft(@Body draftOrderRequest: DraftOrderRequest): DraftOrderResponse


    @GET("draft_orders/{draftFavoriteId}.json")
    suspend fun getProductsIdForDraftFavorite(
        @Path("draftFavoriteId") draftFavoriteId: Long
    ): DraftOrderResponse

    // Get a specific cart by its ID
    @GET("carts/{cartId}.json")
    suspend fun getCartById(
        @Path("cartId") cartId: String
    ): Response<CartResponse>


    @PUT("draft_orders/{draftFavoriteId}.json")
    suspend fun backUpDraftFavorite(
        @Body draftOrderRequest: DraftOrderRequest,
        @Path("draftFavoriteId") draftFavoriteId: Long
    ): Response<DraftOrderResponse>

    // Adress Api Funs
    @GET("customers/{customerId}/addresses.json")
    suspend fun getAddressesOfCustomer(
        @Path("customerId") customerId: Long
    ): AddressesResponse

    @PUT("draft_orders/{draftFavoriteId}/complete.json")
    suspend fun addOrderFromDraftOrder(
        @Path("draftFavoriteId") draftFavoriteId: Long,
        @Query("payment_pending") paymentPending: Boolean
    ): DraftOrderResponse

    @PUT("customers/{customerId}/addresses/{addressId}")
    suspend fun updateAddressOfCustomer(
        @Path("customerId") customerId: Long,
        @Path("addressId") addressId: Long,
        @Body updatedAddress: AddressRequest
    ): Response<AddressResponse>

    @DELETE("customers/{customerId}/addresses/{addressId}")
    suspend fun deleteAddressOfCustomer(
        @Path("customerId") customerId: Long,
        @Path("addressId") addressId: Long
    )

    @POST("customers/{customerId}/addresses.json")
    suspend fun addAddressToCustomer(
        @Path("customerId") customerId: Long,
        @Body address: AddressRequest
    ): AddressResponse

    @GET("orders.json")
    suspend fun getCustomerOrders(@Query("customer_id") customerId: Long): CustomerOrders

    @GET("orders.json")
    suspend fun getOrderDetailsByID(@Query("id") orderId: Long): OrderDetailsResponse

    //coupons
    @GET("price_rules.json")
    suspend fun getAllCoupons() : PriceRuleResponse

}
