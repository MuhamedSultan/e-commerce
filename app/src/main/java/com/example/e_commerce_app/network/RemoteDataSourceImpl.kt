package com.example.e_commerce_app.network

import android.util.Log
import com.example.e_commerce_app.model.address.AddressRequest
import com.example.e_commerce_app.model.address.AddressResponse
import com.example.e_commerce_app.model.address.AddressesResponse
import com.example.e_commerce_app.model.address.testAdd
import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.DraftOrderResponse
import com.example.e_commerce_app.model.cart.PriceRuleResponse
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.custom_collection.CustomCollectionResponse
import com.example.e_commerce_app.model.order_details.OrderDetailsResponse
import com.example.e_commerce_app.model.orders.CustomerOrders
import com.example.e_commerce_app.model.orders.Order
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.user.CustomerDataRequest
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.e_commerce_app.util.ApiState

class RemoteDataSourceImpl : RemoteDataSource {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }


    override suspend fun getAllBrands(): ApiState<SmartCollectionResponse> {
        return try {
            val response = Network.shopifyService.getAllBrands()
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error(e.message.toString())
        }
    }

    override suspend fun signInUser(email: String, password: String): ApiState<UserData> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")

            val userData = firestore.collection("users").document(userId).get().await()
                .toObject(UserData::class.java)
                ?: throw Exception("User data not found")

            ApiState.Success(userData)
        } catch (e: Exception) {
            ApiState.Error("Sign-in failed: ${e.message}")
        }
    }


    override suspend fun registerUser(userData: UserData): ApiState<Unit> {
        return try {
            // Register with Firebase Auth
            val authResult =
                firebaseAuth.createUserWithEmailAndPassword(userData.email, userData.password)
                    .await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")

            // Save user data to Firestore
            saveUserDataToFirestore(userId, userData)

            // Create Shopify customer
            val customerRequest = CustomerRequest(
                CustomerDataRequest(
                    first_name = userData.userName,
                    email = userData.email,
                    verified_email = true,
                    password = userData.password,
                    password_confirmation = userData.password
                )
            )

            // Shopify customer creation result
            val shopifyResult = createShopifyCustomer(customerRequest)

            if (shopifyResult is ApiState.Success) {
                // Handle nullable shopifyCustomerId
                val shopifyCustomerId =
                    shopifyResult.data ?: throw Exception("Shopify customer ID not found")
                saveShopifyCustomerIdToFirestore(userId, shopifyCustomerId)
            }

            ApiState.Success(Unit)
        } catch (e: Exception) {
            ApiState.Error("Registration failed: ${e.message}")
        }
    }


    override suspend fun saveShopifyCustomerIdToFirestore(
        userId: String,
        shopifyCustomerId: String
    ): ApiState<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update("shopifyCustomerId", shopifyCustomerId).await()
            ApiState.Success(Unit)
        } catch (e: Exception) {
            ApiState.Error("Failed to save Shopify customer ID: ${e.message}")
        }
    }

    override suspend fun getAllAddresses(customerId: String): ApiState<AddressesResponse> {
        return try {
            val response = Network.shopifyService.getAddressesOfCustomer(customerId.toLong())
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error(e.message.toString())
        }
    }

    override suspend fun insertAddress(
        customerId: Long,
        addressRequest: AddressRequest
    ): ApiState<AddressResponse> {
        return try {
            val response = Network.shopifyService.addAddressToCustomer(
                customerId,
                addressRequest
            )
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error(e.message.toString())
        }
    }
    override suspend fun deleteAddress(customerId: Long, addressId: Long){
        return try {
            val response = Network.shopifyService.deleteAddressOfCustomer(customerId,addressId)
        }catch (e:Exception){
            Log.e("TAG", "deleteAddress: Failed")
            return
        }
    }
    override suspend fun updateAddress(customerId: Long, addressId: Long, addressRequest: AddressRequest){
        return try {
            val response = Network.shopifyService.updateAddressOfCustomer(
                customerId = customerId,
                addressId = addressId,
                updatedAddress = addressRequest
            )
        }catch (e:Exception){
            Log.e("TAG", "updateAddress: Failed")
            return
        }
    }



    override suspend fun createFavoriteDraft(draftOrderRequest: DraftOrderRequest): ApiState<DraftOrderResponse> {
        return try {
            Log.i("TAG", "createFavoriteDraft: $draftOrderRequest")
            val response = Network.shopifyService.createFavoriteDraft(draftOrderRequest)
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error("Error creating draft order: ${e.message}")
        }

    }

    override suspend fun getProductsIdForDraftFavorite(draftFavoriteId: Long): ApiState<DraftOrderResponse> {

        return try {
            val response = Network.shopifyService.getProductsIdForDraftFavorite(draftFavoriteId)
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error("Error fetching draft order: ${e.message}")
        }
    }

    override suspend fun addOrderFromDraftOrder(draftFavoriteId: Long, paymentPending : Boolean): ApiState<DraftOrderResponse> {

        return try {
            val response = Network.shopifyService.addOrderFromDraftOrder(draftFavoriteId , paymentPending)
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error("Error fetching draft order: ${e.message}")
        }
    }

    override suspend fun backUpDraftFavorite(
        draftOrderRequest: DraftOrderRequest,
        draftFavoriteId: Long
    ): ApiState<DraftOrderResponse> {

        return try {
            val response =
                Network.shopifyService.backUpDraftFavorite(draftOrderRequest, draftFavoriteId)
            if (response.isSuccessful) {
                ApiState.Success(response.body() ?: throw Exception("Draft order backup failed"))
            } else {
                ApiState.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiState.Error("Error backing up draft order: ${e.message}")
        }

    }

    override suspend fun getCartById(cartId: String): ApiState<CartResponse> {
        return try {
            // Make the network call to get the cart by ID
            val response = Network.shopifyService.getCartById(cartId)

            // Check if the response is successful and return the data
            if (response.isSuccessful) {
                ApiState.Success(response.body() ?: throw Exception("Cart not found"))
            } else {
                ApiState.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiState.Error("Error fetching cart: ${e.message}")
        }
    }

    override suspend fun getCustomerOrders(customerId: Long): ApiState<CustomerOrders> {
        return try {
            val response = Network.shopifyService.getCustomerOrders(customerId)
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error(e.message.toString())
        }
    }

    override suspend fun getOrderDetailsByID(orderId: Long): ApiState<OrderDetailsResponse> {
        return try {
            val response =Network.shopifyService.getOrderDetailsByID(orderId)
            ApiState.Success(response)
        }catch (e:Exception){
            ApiState.Error(e.message.toString())
        }
    }


    private suspend fun saveUserDataToFirestore(
        userId: String,
        userData: UserData
    ): ApiState<Unit> {
        return try {
            firestore.collection("users").document(userId).set(userData).await()
            ApiState.Success(Unit)
        } catch (e: Exception) {
            ApiState.Error("Failed to save user data: ${e.message}")
        }
    }


    override suspend fun createShopifyCustomer(customerRequest: CustomerRequest): ApiState<String> {
        return try {
            val response = Network.shopifyService.createCustomer(customerRequest)
            if (response.isSuccessful) {
                val customerId = response.body()?.customer?.id?.toString()
                    ?: throw Exception("Customer ID not found")
                ApiState.Success(customerId)
            } else {
                ApiState.Error("Failed to create Shopify customer: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiState.Error("Error creating Shopify customer: ${e.message}")
        }
    }


    override suspend fun getProductById(productId: Long): ApiState<Product> {
        return try {
            val response = Network.shopifyService.fetchProductById(productId)
            if (response.isSuccessful) {
                ApiState.Success(response.body()?.product ?: throw Exception("Product not found"))
            } else {
                ApiState.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiState.Error("Error fetching product: ${e.message}")
        }
    }


    override suspend fun getRandomProducts(): ApiState<ProductResponse> {
        return try {
            val response = Network.shopifyService.getRandomProducts()
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error(e.message.toString())
        }
    }

    override suspend fun getBrandProducts(brandName: String): ApiState<ProductResponse> {
        return try {
            val response = Network.shopifyService.getBrandProducts(brandName)
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error(e.message.toString())

        }
    }

    override suspend fun getCategories(): ApiState<CustomCollectionResponse> {
        return try {
            val response = Network.shopifyService.getCategories()
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error(e.message.toString())
        }
    }

    override suspend fun getProductsOfSelectedBrand(collectionId: Long): ApiState<ProductResponse> {
        return try {
            val response = Network.shopifyService.getProductsOfSelectedCategory(collectionId)
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error(e.message.toString())
        }
    }
    override suspend fun getAllCoupons() :ApiState<PriceRuleResponse>{
        return try {
            val response = Network.shopifyService.getAllCoupons()
            ApiState.Success(response)
        } catch (e: Exception) {
            ApiState.Error(e.message.toString())
        }
    }

    override suspend fun searchProductsByTitle(title: String): ApiState<ProductResponse> {
        return try {
            val response = Network.shopifyService.searchProductsByTitle(title)
            if (response.isSuccessful) {
                ApiState.Success(response.body() ?: throw Exception("No products found"))
            } else {
                ApiState.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiState.Error("Error searching products: ${e.message}")
        }
    }
   override suspend fun exchangeRate():ApiState<CurrencyResponse>{
       return try {
           val response = CurrencyNetwork.currencyService.getExchangeRates()
           ApiState.Success(response)
       } catch (e: Exception) {
           ApiState.Error(e.message.toString())
       }
   }
}
