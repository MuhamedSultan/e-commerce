package com.example.e_commerce_app.data.source

import com.example.e_commerce_app.model.address.AddressRequest
import com.example.e_commerce_app.model.address.AddressResponse
import com.example.e_commerce_app.model.address.AddressResponseModel
import com.example.e_commerce_app.model.address.AddressesResponse
import com.example.e_commerce_app.model.cart.AppliedDiscount
import com.example.e_commerce_app.model.cart.Cart
import com.example.e_commerce_app.model.cart.CartResponse
import com.example.e_commerce_app.model.cart.DraftOrderDetailsResponse
import com.example.e_commerce_app.model.cart.DraftOrderRequest
import com.example.e_commerce_app.model.cart.DraftOrderResponse
import com.example.e_commerce_app.model.cart.LineItem
import com.example.e_commerce_app.model.custom_collection.CustomCollectionResponse
import com.example.e_commerce_app.model.cart.Customer
import com.example.e_commerce_app.model.cart.CustomerId
import com.example.e_commerce_app.model.cart.DraftOrder
import com.example.e_commerce_app.model.cart.LineItems
import com.example.e_commerce_app.model.custom_collection.CustomCollection
import com.example.e_commerce_app.model.order_details.OrderDetailsResponse
import com.example.e_commerce_app.model.orders.CustomerOrders

import com.example.e_commerce_app.model.product.Image
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.repo.ShopifyRepo
import com.example.e_commerce_app.model.smart_collection.Rule
import com.example.e_commerce_app.model.smart_collection.SmartCollection
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.example.e_commerce_app.model.user.CustomerRequest
import com.example.e_commerce_app.model.user.UserData
import com.example.e_commerce_app.util.ApiState

class FakeShopifyRepository : ShopifyRepo {
    var shouldReturnError = false // Control the outcome of registration
    private val favorites = mutableListOf<Product>()

    private var signInUserResult: ApiState<UserData>? = null

    fun setSignInUserResult(result: ApiState<UserData>) {
        signInUserResult = result
    }


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
            )
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
            )
        )
    )

    private val fakeUserData = UserData(id = 1, email = "user@example.com", userName = "Test User")

    // Updated fake addresses to match the new structure
    private val fakeAddresses = listOf(
        AddressResponse(
            customer_address = AddressResponseModel(
                address1 = "123 Main St",
                address2 = null,
                city = "New York",
                company = null,
                country = "USA",
                countryCode = "US",
                countryName = "United States",
                customerId = 1,
                default = true,
                firstName = "John",
                id = 1,
                lastName = "Doe",
                name = "John Doe",
                phone = "123-456-7890",
                province = "NY",
                provinceCode = "NY",
                zip = "10001"
            )
        ),
        AddressResponse(
            customer_address = AddressResponseModel(
                address1 = "456 Elm St",
                address2 = null,
                city = "Los Angeles",
                company = null,
                country = "USA",
                countryCode = "US",
                countryName = "United States",
                customerId = 1,
                default = false,
                firstName = "Jane",
                id = 2,
                lastName = "Smith",
                name = "Jane Smith",
                phone = "987-654-3210",
                province = "CA",
                provinceCode = "CA",
                zip = "90001"
            )
        )
    )


    private val fakeCartResponse = CartResponse(
        carts = listOf(
            Cart(
                id = "cart123",
                lineItems = fakeProducts.map { mapProductToLineItem(it) }
            )
        )
    )


    private fun mapProductToLineItem(product: Product): LineItem {
        return LineItem(
            id = product.id.toString(),
            productId = product.id.toString(),
            quantity = 1,
            title = product.title,
            price = "20.00",
            totalPrice = "20.00",
            imageUrl = product.image.src
        )
    }

    override suspend fun getAllBrands(): ApiState<SmartCollectionResponse> {
        return ApiState.Error("Not implemented")
    }

    override suspend fun getRandomProducts(): ApiState<ProductResponse> {
        return ApiState.Error("Not implemented")
    }
    override suspend fun getBrandProducts(brandName: String): ApiState<ProductResponse> {
        return ApiState.Error("Not implemented")
    }
    override suspend fun signInUser(email: String, password: String): ApiState<UserData> {
        return signInUserResult ?: ApiState.Error("No result set")
    }


    override suspend fun registerUser(userData: UserData): ApiState<Unit> {
        return if (shouldReturnError) {
            ApiState.Error("User registration failed")
        } else {
            ApiState.Success(Unit) // Simulate successful registration
        }
    }

    override suspend fun createShopifyCustomer(customerRequest: CustomerRequest): ApiState<String> {
        return ApiState.Success("fake_customer_id")
    }

    override suspend fun getProductById(productId: Long): ApiState<Product> {
        return ApiState.Success(fakeProducts.firstOrNull { it.id == productId }
            ?: Product())
    }

    override suspend fun getCategories(): ApiState<CustomCollectionResponse> {
        // Sample image data
        val sampleImage = com.example.e_commerce_app.model.custom_collection.Image(
            alt = "Sample image alt text",
            created_at = "2024-01-01T00:00:00Z",
            height = 500,
            src = "https://example.com/sample-image.png",
            width = 500
        )


        // Sample custom collection data
        val sampleCustomCollection = CustomCollection(
            admin_graphql_api_id = "collection_api_id_1",
            body_html = "<p>This is a sample collection description</p>",
            handle = "sample-collection",
            id = 1L,
            image = sampleImage,
            published_at = "2024-01-01T12:00:00Z",
            published_scope = "global",
            sort_order = "manual",
            template_suffix = "",
            title = "Sample Collection",
            updated_at = "2024-01-02T12:00:00Z"
        )

        // Sample response with the custom collection
        val customCollectionResponse = CustomCollectionResponse(
            custom_collections = listOf(sampleCustomCollection)
        )

        // Returning ApiState with the sample response
        return ApiState.Success(data = customCollectionResponse)
    }


    override suspend fun getProductsOfSelectedCategory(collectionId: Long): ApiState<ProductResponse> {
        return ApiState.Success(ProductResponse(products = fakeProducts))
    }

    override suspend fun addToFavorite(product: Product) {
        // Simulate adding a product to favorites
        favorites.add(product)
    }
    override suspend fun removeFavorite(product: Product) {
        // Simulate removing a product from favorites
        favorites.remove(product)
    }

    override suspend fun getAllFavorites(shopifyCustomerId: String): List<Product> {
        // Simulate returning the list of favorite products
        return favorites.toList() // Return a copy of the list
    }

    override suspend fun searchProductsByTitle(title: String): ApiState<ProductResponse> {
        val filteredProducts = fakeProducts.filter { it.title.contains(title, ignoreCase = true) }
        return ApiState.Success(ProductResponse(products = filteredProducts))
    }

    override suspend fun saveShopifyCustomerId(
        userId: String,
        shopifyCustomerId: String
    ): ApiState<Unit> {
        return ApiState.Success(Unit)
    }

    override suspend fun getAllAddresses(customerId: String): ApiState<AddressesResponse> {
        val addressModels = fakeAddresses.map { it.customer_address }

        return ApiState.Success(AddressesResponse(addressResponses = addressModels))
    }

    override suspend fun insertAddress(
        customerId: Long,
        addressRequest: AddressRequest
    ): ApiState<AddressResponse> {
        // Extracting address details from the AddressRequest
        val addressDetails = addressRequest.address

        // Creating a new AddressResponseModel from the addressRequest
        val newAddress = AddressResponse(
            customer_address = AddressResponseModel(
                address1 = addressDetails.address1,
                address2 = addressDetails.address2, // Nullable, take from addressRequest
                city = addressDetails.city,
                company = null, // Set to null as per your original logic
                country = addressDetails.country, // Pass the country from addressRequest
                countryCode = "US", // Fixed value for USA
                countryName = "United States", // Fixed value for USA
                customerId = customerId,
                default = false, // Assuming new addresses are not default
                firstName = addressDetails.first_name,
                id = 3, // Use a suitable logic to assign ID
                lastName = addressDetails.last_name,
                name = "${addressDetails.first_name} ${addressDetails.last_name}", // Combine first and last names
                phone = addressDetails.phone,
                province = addressDetails.province,
                provinceCode = "NY", // Hardcoded as per your logic; can be dynamic if needed
            )
        )

        // Simulate inserting address and returning the success state
        return ApiState.Success(newAddress)
    }

    override suspend fun createFavoriteDraft(draftOrderRequest: DraftOrderRequest): ApiState<DraftOrderResponse> {
        // Create a sample customer
        val testCustomer = Customer(
            accepts_marketing = true,
            accepts_marketing_updated_at = "2024-01-01T00:00:00Z",
            admin_graphql_api_id = "customer_api_id",
            created_at = "2024-01-01T00:00:00Z",
            currency = "USD",
            default_address = null,
            email = "test@example.com",
            first_name = "Test",
            id = 1L,
            last_name = "User",
            last_order_id = null,
            last_order_name = null,
            marketing_opt_in_level = null,
            multipass_identifier = null,
            note = null,
            orders_count = 5,
            phone = "123-456-7890",
            state = "NY",
            tags = "test_tag",
            tax_exempt = false,
            tax_exemptions = emptyList(),
            total_spent = "100.00",
            updated_at = "2024-01-01T00:00:00Z",
            verified_email = true
        )

        // Create sample line items based on DraftOrder structure
        val testLineItems = listOf(
            LineItems(
                title = "Test Product",
                price = "20.00",
                quantity = 2
            )
        )

        // Assuming testLineItems has the necessary data to derive the required fields
        val mappedLineItems = testLineItems.mapIndexed { index, lineItem ->
            LineItem(
                id = "line_item_id_$index", // Generate a unique ID for each line item, adjust as necessary
                productId = "product_id_$index", // Use a real product ID or generate one based on your context
                quantity = lineItem.quantity,
                title = lineItem.title,
                price = lineItem.price,
                totalPrice = (lineItem.price.toDouble() * lineItem.quantity).toString(), // Calculate total price
                imageUrl = "http://example.com/image_$index.jpg" // Use a real image URL or generate one
            )
        }


        // Create sample applied discount
        val testDiscount = AppliedDiscount(
            description = "Test discount description",
            value_type = "fixed_amount",
            value = "5.00",
            amount = "5.00",
            title = "Test Discount"
        )

        // Create DraftOrder
        val testDraftOrder = DraftOrder(
            line_items = testLineItems,
            applied_discount = testDiscount,
            customer = CustomerId(id = testCustomer.id),
            use_customer_default_address = true
        )

        // Create the DraftOrderDetailsResponse
        val testDraftOrderDetails = DraftOrderDetailsResponse(
            admin_graphql_api_id = "draft_order_api_id",
            applied_discount = testDiscount,
            billing_address = null,
            completed_at = null,
            created_at = "2024-01-01T00:00:00Z",
            currency = "USD",
            customer = testCustomer,
            email = "test@example.com",
            id = 1L, // Use a test ID
            invoice_sent_at = null,
            invoice_url = null,
            line_items = mappedLineItems, // Use the mapped list here
            name = "Draft Order #1",
            note = null,
            note_attributes = emptyList(),
            order_id = null,
            payment_terms = null,
            shipping_address = null,
            status = "open",
            subtotal_price = "40.00", // Total of line items
            tags = "test_tag",
            tax_exempt = false,
            tax_lines = emptyList(),
            taxes_included = true,
            total_price = "40.00",
            total_tax = "0.00",
            updated_at = "2024-01-01T00:00:00Z"
        )

        // Return the success state with initialized test data
        return ApiState.Success(DraftOrderResponse(draft_order = testDraftOrderDetails))
    }

    override suspend fun getProductsIdForDraftFavorite(draftFavoriteId: Long): ApiState<DraftOrderResponse> {
        val testCustomer = Customer(
            accepts_marketing = true,
            accepts_marketing_updated_at = "2024-01-01T00:00:00Z",
            admin_graphql_api_id = "customer_api_id",
            created_at = "2024-01-01T00:00:00Z",
            currency = "USD",
            default_address = null,
            email = "test@example.com",
            first_name = "Test",
            id = 1L,
            last_name = "User",
            last_order_id = null,
            last_order_name = null,
            marketing_opt_in_level = null,
            multipass_identifier = null,
            note = null,
            orders_count = 5,
            phone = "123-456-7890",
            state = "NY",
            tags = "test_tag",
            tax_exempt = false,
            tax_exemptions = emptyList(),
            total_spent = "100.00",
            updated_at = "2024-01-01T00:00:00Z",
            verified_email = true
        )

        val testLineItems = listOf(
            LineItem(
                id = "1",
                productId = "TEST_PRODUCT_ID",
                quantity = 2,
                title = "Test Product",
                price = "20.00",
                totalPrice = "40.00", // Assuming total price calculation
                imageUrl = "https://example.com/test_product.jpg" // Sample image URL
            )
        )

        val testDraftOrderDetails = DraftOrderDetailsResponse(
            admin_graphql_api_id = "draft_order_api_id",
            applied_discount = null,
            billing_address = null,
            completed_at = null,
            created_at = "2024-01-01T00:00:00Z",
            currency = "USD",
            customer = testCustomer,
            email = "test@example.com",
            id = draftFavoriteId,
            invoice_sent_at = null,
            invoice_url = null,
            line_items = testLineItems,
            name = "Draft Order #$draftFavoriteId",
            note = null,
            note_attributes = emptyList(),
            order_id = null,
            payment_terms = null,
            shipping_address = null,
            status = "open",
            subtotal_price = "40.00",
            tags = "test_tag",
            tax_exempt = false,
            tax_lines = emptyList(),
            taxes_included = true,
            total_price = "40.00",
            total_tax = "0.00",
            updated_at = "2024-01-01T00:00:00Z"
        )

        return ApiState.Success(DraftOrderResponse(draft_order = testDraftOrderDetails))
    }

    override suspend fun backUpDraftFavorite(
        draftOrderRequest: DraftOrderRequest,
        draftFavoriteId: Long
    ): ApiState<DraftOrderResponse> {
        val testCustomer = Customer(
            accepts_marketing = false,
            accepts_marketing_updated_at = null,
            admin_graphql_api_id = "customer_api_id",
            created_at = "2024-01-01T00:00:00Z",
            currency = "USD",
            default_address = null,
            email = "test@example.com",
            first_name = "Test",
            id = 1L,
            last_name = null,
            last_order_id = null,
            last_order_name = null,
            marketing_opt_in_level = null,
            multipass_identifier = null,
            note = null,
            orders_count = null,
            phone = null,
            state = "NY",
            tags = "test_tag",
            tax_exempt = false,
            tax_exemptions = emptyList(),
            total_spent = null,
            updated_at = "2024-01-01T00:00:00Z",
            verified_email = true
        )

        val testLineItems = listOf(
            LineItem(
                id = "1",
                productId = "TEST_PRODUCT_ID",
                quantity = 2,
                title = "Test Product",
                price = "20.00",
                totalPrice = "40.00",
                imageUrl = "https://example.com/test_product.jpg"
            )
        )

        val draftOrderDetails = DraftOrderDetailsResponse(
            admin_graphql_api_id = "draft_order_api_id",
            applied_discount = null,
            billing_address = null,
            completed_at = null,
            created_at = "2024-01-01T00:00:00Z",
            currency = "USD",
            customer = testCustomer,
            email = "test@example.com",
            id = draftFavoriteId,
            invoice_sent_at = null,
            invoice_url = null,
            line_items = testLineItems,
            name = "Draft Order #$draftFavoriteId",
            note = null,
            note_attributes = emptyList(),
            order_id = null,
            payment_terms = null,
            shipping_address = null,
            status = "open",
            subtotal_price = "40.00",
            tags = "test_tag",
            tax_exempt = false,
            tax_lines = emptyList(),
            taxes_included = true,
            total_price = "40.00",
            total_tax = "0.00",
            updated_at = "2024-01-01T00:00:00Z"
        )

        return ApiState.Success(DraftOrderResponse(draft_order = draftOrderDetails))
    }


    override suspend fun getCartById(cartId: String): ApiState<CartResponse> {
        return ApiState.Success(fakeCartResponse)
    }

    override suspend fun getCustomerOrders(customerId: Long): ApiState<CustomerOrders> {
        return ApiState.Error("An error occurred while fetching customer orders.") // Adjust the error message as needed
    }

    override suspend fun getOrderDetailsByID(orderId: Long): ApiState<OrderDetailsResponse> {
        TODO("Not yet implemented")
    }
}
