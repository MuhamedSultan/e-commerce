package com.example.e_commerce_app.data.source

import com.example.e_commerce_app.model.address.AddressRequest
import com.example.e_commerce_app.model.address.AddressResponse
import com.example.e_commerce_app.model.address.AddressResponseModel
import com.example.e_commerce_app.model.address.AddressesResponse
import com.example.e_commerce_app.model.cart.Cart
import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.cart.Customer
import com.example.e_commerce_app.model.cart.DraftOrderDetailsResponse
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.DraftOrderResponse
import com.example.e_commerce_app.model.cart.LineItem
import com.example.e_commerce_app.model.cart.PriceRuleResponse
import com.example.e_commerce_app.model.currencyResponse.CurrencyResponse
import com.example.e_commerce_app.model.custom_collection.CustomCollectionResponse
import com.example.e_commerce_app.model.order_details.OrderDetailsResponse
import com.example.e_commerce_app.model.orders.CustomerOrders
import com.example.e_commerce_app.model.product.Image
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.network.RemoteDataSource
import com.example.e_commerce_app.util.ApiState

class FakeRemoteDataSource : RemoteDataSource {

    private val fakeProducts = listOf(
        Product(
            id = 1,
            title = "Fake Product 1",
            image = Image(
                admin_graphql_api_id = "api_id_1",
                alt = "alt_text_1",
                created_at = "2024-01-01",
                height = 500,
                id = 1,
                position = 1,
                product_id = 1,
                src = "fake_image_1.png",
                updated_at = "2024-01-02",
                variant_ids = listOf(),
                width = 500
            ),
        ),
        Product(
            id = 2,
            title = "Fake Product 2",
            image = Image(
                admin_graphql_api_id = "api_id_2",
                alt = "alt_text_2",
                created_at = "2024-01-01",
                height = 500,
                id = 2,
                position = 1,
                product_id = 2,
                src = "fake_image_2.png",
                updated_at = "2024-01-02",
                variant_ids = listOf(),
                width = 500
            ),
        )
    )

    private val fakeDraftOrders = mutableListOf<DraftOrderResponse>()
    private var draftOrderCounter = 1L

    override suspend fun getAllBrands(): ApiState<SmartCollectionResponse> {
        val fakeResponse = SmartCollectionResponse(listOf())
        return ApiState.Success(fakeResponse)
    }

    override suspend fun getRandomProducts(): ApiState<ProductResponse> {
        val fakeResponse = ProductResponse(fakeProducts)
        return ApiState.Success(fakeResponse)
    }

    override suspend fun getProductById(productId: Long): ApiState<Product> {
        val product = fakeProducts.find { it.id == productId }
        return if (product != null) {
            ApiState.Success(product)
        } else {
            ApiState.Error("Product not found")
        }
    }

    override suspend fun getBrandProducts(brandName: String): ApiState<ProductResponse> {
        return ApiState.Success(ProductResponse(fakeProducts))
    }

    override suspend fun getCategories(): ApiState<CustomCollectionResponse> {
        val fakeResponse = CustomCollectionResponse(listOf())
        return ApiState.Success(fakeResponse)
    }

    override suspend fun getProductsOfSelectedBrand(collectionId: Long): ApiState<ProductResponse> {
        return ApiState.Success(ProductResponse(fakeProducts))
    }

    override suspend fun searchProductsByTitle(title: String): ApiState<ProductResponse> {
        val filteredProducts = fakeProducts.filter { it.title.contains(title, ignoreCase = true) }
        return ApiState.Success(ProductResponse(filteredProducts))
    }

    override suspend fun createShopifyCustomer(customerRequest: CustomerRequest): ApiState<String> {
        return ApiState.Success("FakeCustomerId123")
    }

    override suspend fun signInUser(email: String, password: String): ApiState<UserData> {
        return ApiState.Success(
            UserData(
                id = 1L,
                userName = "Test User",
                email = email,
                password = password,
                phoneNumber = "123456789"
            )
        )
    }

    override suspend fun registerUser(userData: UserData): ApiState<Unit> {
        return ApiState.Success(Unit)
    }

    override suspend fun saveShopifyCustomerIdToFirestore(
        userId: String,
        shopifyCustomerId: String
    ): ApiState<Unit> {
        return ApiState.Success(Unit)
    }

    override suspend fun getAllAddresses(customerId: String): ApiState<AddressesResponse> {
        return ApiState.Success(AddressesResponse(listOf()))
    }

    override suspend fun insertAddress(
        customerId: Long,
        addressRequest: AddressRequest
    ): ApiState<AddressResponse> {
        val address = addressRequest.address

        val fakeAddressResponseModel = AddressResponseModel(
            address1 = address.address1,
            address2 = address.address2,
            city = address.city,
            company = null,
            country = address.country,
            countryCode = null,
            countryName = null,
            customerId = customerId,
            default = null,
            id = 1L,
            firstName = address.first_name,
            lastName = address.last_name,
            name = "${address.first_name} ${address.last_name}",
            phone = address.phone,
            province = address.province,
            provinceCode = null,
            zip = null
        )
        val response = AddressResponse(customer_address = fakeAddressResponseModel)
        return ApiState.Success(response)
    }

    override suspend fun createFavoriteDraft(draftOrderRequest: DraftOrderRequest): ApiState<DraftOrderResponse> {
        // Create a fake draft order details response
        val fakeDraftOrderDetails = DraftOrderDetailsResponse(
            admin_graphql_api_id = "fake_id_${draftOrderCounter++}",
            applied_discount = null,
            billing_address = null,
            completed_at = null,
            created_at = "2024-10-08T12:00:00Z",  // Sample date string
            currency = "USD",
            customer = Customer(
                accepts_marketing = false,
                accepts_marketing_updated_at = null,
                admin_graphql_api_id = "customer_fake_id",
                created_at = "2024-10-08T12:00:00Z",
                currency = "USD",
                default_address = null,
                email = "fakeemail@example.com",
                first_name = "John",
                id = 1L,
                last_name = "Doe",
                last_order_id = null,
                last_order_name = null,
                marketing_opt_in_level = null,
                multipass_identifier = null,
                note = null,
                orders_count = 0,
                phone = "1234567890",
                state = "NY",
                tags = null,
                tax_exempt = false,
                tax_exemptions = listOf(),
                total_spent = "100.00",
                updated_at = "2024-10-08T12:00:00Z",
                verified_email = true
            ),
            email = "fakeemail@example.com",
            id = draftOrderCounter.toLong(),  // Assuming id is based on the counter
            invoice_sent_at = null,
            invoice_url = null,
            line_items = listOf(),  // Populate as needed
            name = "Draft Order #${draftOrderCounter}",
            note = null,
            note_attributes = listOf(),
            order_id = null,
            payment_terms = null,
            shipping_address = null,
            status = "pending",
            subtotal_price = "100.00",
            tags = null,
            tax_exempt = false,
            tax_lines = listOf(),
            taxes_included = false,
            total_price = "100.00",
            total_tax = "0.00",
            updated_at = "2024-10-08T12:00:00Z"
        )

        // Create the DraftOrderResponse using the fake details
        val fakeDraftOrder = DraftOrderResponse(draft_order = fakeDraftOrderDetails)
        fakeDraftOrders.add(fakeDraftOrder)  // Assuming fakeDraftOrders is a list to store drafts
        return ApiState.Success(fakeDraftOrder)
    }

    override suspend fun getProductsIdForDraftFavorite(draftFavoriteId: Long): ApiState<DraftOrderResponse> {
        // Find the draft order based on the draftFavoriteId
        val draftOrder = fakeDraftOrders.find { it.draft_order.id == draftFavoriteId }

        return if (draftOrder != null) {
            ApiState.Success(draftOrder)
        } else {
            ApiState.Error("Draft not found")
        }
    }

    override suspend fun addOrderFromDraftOrder(
        draftFavoriteId: Long,
        paymentPending: Boolean
    ): ApiState<DraftOrderResponse> {
        TODO("Not yet implemented")
    }




    override suspend fun backUpDraftFavorite(
        draftOrderRequest: DraftOrderRequest,
        draftFavoriteId: Long
    ): ApiState<DraftOrderResponse> {
        val existingDraftOrder = fakeDraftOrders.find { it.draft_order.id == draftFavoriteId }

        return if (existingDraftOrder != null) {
            ApiState.Success(existingDraftOrder)
        } else {
            ApiState.Error("Draft not found for backup")
        }
    }

    override suspend fun getCartById(cartId: String): ApiState<CartResponse> {
        // Return a fake cart response
        val fakeCart = CartResponse(
            carts = listOf(
                Cart(
                    id = cartId,
                    lineItems = listOf(
                        LineItem(
                            id = "item1",
                            productId = "prod1",
                            quantity = 2,
                            title = "Sample Product",
                            price = "20.00",
                            totalPrice = "40.00",
                            imageUrl = "sample_image_url"
                        ),
                        LineItem(
                            id = "item2",
                            productId = "prod2",
                            quantity = 1,
                            title = "Another Product",
                            price = "15.00",
                            totalPrice = "15.00",
                            imageUrl = "another_image_url"
                        )
                    )
                )
            )
        )
        return ApiState.Success(fakeCart)
    }

    override suspend fun getCustomerOrders(customerId: Long): ApiState<CustomerOrders> {
        return ApiState.Success(CustomerOrders(listOf()))
    }

    override suspend fun getOrderDetailsByID(orderId: Long): ApiState<OrderDetailsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCoupons(): ApiState<PriceRuleResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun exchangeRate(): ApiState<CurrencyResponse> {
        TODO("Not yet implemented")
    }
}
