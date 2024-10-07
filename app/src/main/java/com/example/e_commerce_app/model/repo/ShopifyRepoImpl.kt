package com.example.e_commerce_app.model.repo

import com.example.e_commerce_app.db.LocalDataSource
import com.example.e_commerce_app.model.address.AddressRequest
import com.example.e_commerce_app.model.address.AddressResponse
import com.example.e_commerce_app.model.address.AddressesResponse
import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.cart.DeleteProductResponse
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.DraftOrderResponse
import com.example.e_commerce_app.model.cart.UpdateCartItemRequest
import com.example.e_commerce_app.model.cart.UpdateCartItemResponse
import com.example.e_commerce_app.model.custom_collection.CustomCollectionResponse
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.network.RemoteDataSource
import com.example.e_commerce_app.util.ApiState

class ShopifyRepoImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource? = null
) : ShopifyRepo {
    override suspend fun getAllBrands(): ApiState<SmartCollectionResponse> {
        return remoteDataSource.getAllBrands()
    }
//    override fun getAllCartProducts():Flow<CartResponse> {
//        return remoteDataSource.getAllCartProducts()
//    }

//    override suspend fun getDraftIds(customerId: String)=
//        flow {
//            val draftIDs = mutableListOf<String>()
//            emit(ApiState.Loading)
//            remoteDataSource.getDraftIds(customerId).addOnSuccessListener {
//                if (it.exists()) {
//                    draftIDs.add(it.data?.get(Constants.DRAFT_FAVORITE_ID) as String)
//                    draftIDs.add(it.data?.get(Constants.DRAFT_CART_ID) as String)
//                }
//            }.await()
//            emit(ApiState.Success(draftIDs))
//        }.catch {
//            emit(ApiState.Error("${it.message}"))
//        }


    override suspend fun signInUser(email: String, password: String): ApiState<UserData> {
        return remoteDataSource.signInUser(email, password)
    }

    override suspend fun registerUser(userData: UserData): ApiState<Unit> {
        return remoteDataSource.registerUser(userData)
    }

    override suspend fun createShopifyCustomer(customerRequest: CustomerRequest): ApiState<String> {
        return remoteDataSource.createShopifyCustomer(customerRequest)
    }


    override suspend fun getProductById(productId: Long): ApiState<Product> {
        return remoteDataSource.getProductById(productId)
    }

    override suspend fun getCategories(): ApiState<CustomCollectionResponse> {
        return remoteDataSource.getCategories()
    }

    override suspend fun getProductsOfSelectedCategory(collectionId: Long): ApiState<ProductResponse> {
        return remoteDataSource.getProductsOfSelectedBrand(collectionId)
    }

    override suspend fun addToFavorite(product: Product) {

        localDataSource?.addToFavorite(product)

    }

//    override suspend fun getAllFavorites(): List<Product> {
//        return localDataSource?.getAllFavorites() ?: emptyList()
//    }

    override suspend fun removeFavorite(product: Product) {
        localDataSource?.removeFavorite(product)
    }

    override suspend fun getAllFavorites(shopifyCustomerId: String): List<Product> {
        return localDataSource?.getAllFavorites(shopifyCustomerId) ?: emptyList()
    }

    override suspend fun searchProductsByTitle(title: String): ApiState<ProductResponse> {
        return remoteDataSource.searchProductsByTitle(title)
    }

    override suspend fun saveShopifyCustomerId(
        userId: String,
        shopifyCustomerId: String
    ): ApiState<Unit> {
        return remoteDataSource.saveShopifyCustomerIdToFirestore(userId, shopifyCustomerId)
    }

    override suspend fun getAllAddresses(customerId: String): ApiState<AddressesResponse> {
        return remoteDataSource.getAllAddresses(customerId)
    }

    override suspend fun insertAddress(
        customerId: Long,
        addressResponse: AddressRequest
    ): ApiState<AddressResponse> {
        return remoteDataSource.insertAddress(customerId, addressResponse)
    }

    override suspend fun createFavoriteDraft(draftOrderRequest: DraftOrderRequest): ApiState<DraftOrderResponse> {
        return remoteDataSource.createFavoriteDraft(draftOrderRequest)
    }

    override suspend fun getProductsIdForDraftFavorite(draftFavoriteId: Long): ApiState<DraftOrderResponse> {
        return remoteDataSource.getProductsIdForDraftFavorite(draftFavoriteId)
    }

    override suspend fun backUpDraftFavorite(
        draftOrderRequest: DraftOrderRequest,
        draftFavoriteId: Long
    ): ApiState<DraftOrderResponse> {

        return remoteDataSource.backUpDraftFavorite(draftOrderRequest, draftFavoriteId)
    }

    override suspend fun getCartById(cartId: String): ApiState<CartResponse> {
        return remoteDataSource.getCartById(cartId)
    }


    override suspend fun getRandomProducts(): ApiState<ProductResponse> {
        return remoteDataSource.getRandomProducts()
    }

    override suspend fun getBrandProducts(brandName: String): ApiState<ProductResponse> {
        return remoteDataSource.getBrandProducts(brandName)
    }

}