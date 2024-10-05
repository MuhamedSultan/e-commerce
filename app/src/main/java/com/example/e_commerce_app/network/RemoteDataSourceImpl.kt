package com.example.e_commerce_app.network

import com.example.e_commerce_app.model.custom_collection.CustomCollectionResponse
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
import retrofit2.Response

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
            // Sign in with Firebase Auth
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")

            // Fetch user data from Firestore
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
            createShopifyCustomer(customerRequest)

            ApiState.Success(Unit)
        } catch (e: Exception) {
            ApiState.Error("Registration failed: ${e.message}")
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


    override suspend fun createShopifyCustomer(customerRequest: CustomerRequest): ApiState<Unit> {
        return try {
            val response = Network.shopifyService.createCustomer(customerRequest)
            if (response.isSuccessful) {
                ApiState.Success(Unit)
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
}
