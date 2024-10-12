package com.example.e_commerce_app.model

import com.example.e_commerce_app.model.custom_collection.CustomCollection
import com.example.e_commerce_app.model.custom_collection.CustomCollectionResponse
import com.example.e_commerce_app.model.order_details.DefaultAddress
import com.example.e_commerce_app.model.orders.ClientDetails
import com.example.e_commerce_app.model.orders.Customer
import com.example.e_commerce_app.model.orders.CustomerOrders
import com.example.e_commerce_app.model.orders.EmailMarketingConsent
import com.example.e_commerce_app.model.orders.LineItem
import com.example.e_commerce_app.model.orders.Order
import com.example.e_commerce_app.model.orders.PriceSet
import com.example.e_commerce_app.model.product.Option
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.product.ProductResponse
import com.example.e_commerce_app.model.product.Variant
import com.example.e_commerce_app.model.smart_collection.Image
import com.example.e_commerce_app.model.smart_collection.Rule
import com.example.e_commerce_app.model.smart_collection.SmartCollection
import com.example.e_commerce_app.model.smart_collection.SmartCollectionResponse
import com.google.type.Money

object ApiResponse {

    val brands = SmartCollectionResponse(
        listOf(
            SmartCollection(
                id = 482200912187,
                handle = "adidas",
                title = "ADIDAS",
                updated_at = "2024-10-12T08:15:20-04:00",
                body_html = "Adidas collection",
                published_at = "2024-10-01T03:36:08-04:00",
                sort_order = "best-selling",
                template_suffix = "",
                disjunctive = false,
                rules = listOf(
                    Rule(
                        column = "title",
                        relation = "contains",
                        condition = "ADIDAS"
                    )
                ),
                image = Image(
                    alt = "",
                    created_at = "2024-10-12T08:15:20-04:00",
                    height = 800,
                    src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/products/sample_image.jpg?v=1727768169",
                    width = 600
                ),
                admin_graphql_api_id = "gid://shopify/ProductImage/123456789",
                published_scope = ""
            ), SmartCollection(
                id = 482201010491,
                handle = "asics-tiger",
                title = "ASICS TIGER",
                updated_at = "2024-10-12T08:15:20-04:00",
                body_html = "Adidas collection",
                published_at = "2024-10-01T03:36:08-04:00",
                sort_order = "best-selling",
                template_suffix = "",
                disjunctive = false,
                rules = listOf(
                    Rule(
                        column = "title",
                        relation = "contains",
                        condition = "ADIDAS"
                    )
                ),
                image = Image(
                    alt = "",
                    created_at = "2024-10-12T08:15:20-04:00",
                    height = 800,
                    src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/products/sample_image.jpg?v=1727768169",
                    width = 600
                ),
                admin_graphql_api_id = "gid://shopify/ProductImage/123456789",
                published_scope = ""
            )
        )
    )


    val customCollections = CustomCollectionResponse(
        listOf(
            CustomCollection(
                id = 482200682811,
                handle = "frontpage",
                title = "Home page",
                updated_at = "2024-10-11T19:05:32-04:00",
                body_html = "",
                published_at = "2024-10-01T03:27:37-04:00",
                sort_order = "best-selling",
                template_suffix = "",
                published_scope = "global",
                admin_graphql_api_id = "gid://shopify/Collection/482200682811",
                image = com.example.e_commerce_app.model.custom_collection.Image(
                    created_at = "2024-10-01T03:36:25-04:00",
                    alt = "",
                    width = 736,
                    height = 490,
                    src =
                    "https://cdn.shopify.com/s/files/1/0899/5212/5243/collections/3b6a545a8f309a6085625bcadcb19712.jpg?v=1727768185"
                )
            ),
            CustomCollection(
                id = 482201338171,
                handle = "kid",
                title = "KID",
                updated_at = "2024-10-12T09:55:20-04:00",
                body_html = "Collection for kids",
                published_at = "2024-10-01T03:36:25-04:00",
                sort_order = "best-selling",
                template_suffix = "",
                published_scope = "web",
                admin_graphql_api_id = "gid://shopify/Collection/482201338171",
                image = com.example.e_commerce_app.model.custom_collection.Image(
                    created_at = "2024-10-01T03:36:25-04:00",
                    alt = "",
                    width = 736,
                    height = 490,
                    src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/collections/3b6a545a8f309a6085625bcadcb19712.jpg?v=1727768185"
                )
            ),
            CustomCollection(
                id = 482201272635,
                handle = "men",
                title = "MEN",
                updated_at = "2024-10-12T05:35:18-04:00",
                body_html = "Collection for men",
                published_at = "2024-10-01T03:36:21-04:00",
                sort_order = "best-selling",
                template_suffix = "",
                published_scope = "web",
                admin_graphql_api_id = "gid://shopify/Collection/482201272635",
                image = com.example.e_commerce_app.model.custom_collection.Image(
                    created_at = "2024-10-01T03:36:21-04:00",
                    alt = "",
                    width = 480,
                    height = 200,
                    src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/collections/cde37406b76337f4438c62f57be75df2.jpg?v=1727768181"
                )
            ),
            CustomCollection(
                id = 482201370939,
                handle = "sale",
                title = "SALE",
                updated_at = "2024-10-11T19:05:32-04:00",
                body_html = "On sale",
                published_at = "2024-10-01T03:36:26-04:00",
                sort_order = "best-selling",
                template_suffix = "",
                published_scope = "web",
                admin_graphql_api_id = "gid://shopify/Collection/482201370939",
                image = com.example.e_commerce_app.model.custom_collection.Image(
                    created_at = "2024-10-01T03:36:27-04:00",
                    alt = "",
                    width = 480,
                    height = 200,
                    src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/collections/82c7b0b668b962bb4ffae786c8e827cd.jpg?v=1727768187"
                )
            ),
            CustomCollection(
                id = 482201305403,
                handle = "women",
                title = "WOMEN",
                updated_at = "2024-10-11T19:35:23-04:00",
                body_html = "Collection for women",
                published_at = "2024-10-01T03:36:23-04:00",
                sort_order = "best-selling",
                template_suffix = "",
                published_scope = "web",
                admin_graphql_api_id = "gid://shopify/Collection/482201305403",
                image = com.example.e_commerce_app.model.custom_collection.Image(
                    created_at = "2024-10-01T03:36:24-04:00",
                    alt = "",
                    width = 480,
                    height = 200,
                    src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/collections/0711d7a9ab22e1d866c244756574349b.jpg?v=1727768184"
                )
            )
        )
    )
    val testCategoriesProducts = ProductResponse(
        listOf(
            Product(
                id = 9828914725179,
                title = "VANS |AUTHENTIC | LO PRO | BURGANDY/WHITE",
                body_html = "The forefather of the Vans family, the original Vans Authentic was introduced in 1966 and nearly 4 decades later is still going strong, its popularity extending from the original fans - skaters and surfers to all sorts. Now remodeled into a low top lace-up with a slim silhouette, the Vans Authentic Lo Pro features sturdy canvas uppers with lower sidewalls, metal eyelets, and low profile mini waffle outsoles for a lightweight feel.",
                vendor = "VANS",
                product_type = "SHOES",
                created_at = "2024-10-01T03:32:07-04:00",
                handle = "vans-authentic-lo-pro-burgandy-white",
                updated_at = "2024-10-11T19:05:32-04:00",
                published_at = "2024-10-01T03:32:07-04:00",
                template_suffix = null,
                published_scope = "global",
                tags = "egnition-sample-data, men, sale, summer, vans",
                status = "active",
                admin_graphql_api_id = "gid://shopify/Product/9828914725179",
                variants = listOf(
                    Variant(
                        id = 49540005036347,
                        product_id = 9828914725179,
                        title = "4 / burgandy",
                        price = "29.00",
                        position = 1,
                        inventory_policy = "deny",
                        compare_at_price = "99.95",
                        option1 = "4",
                        option2 = "burgandy",
                        option3 = "",
                        created_at = "2024-10-01T03:32:07-04:00",
                        updated_at = "2024-10-11T19:02:51-04:00",
                        taxable = true,
                        barcode = "",
                        fulfillment_service = "manual",
                        grams = 0,
                        inventory_management = "shopify",
                        requires_shipping = true,
                        sku = "VN-01-burgandy-4",
                        weight = 0.0,
                        weight_unit = "kg",
                        inventory_quantity = 5158,
                        old_inventory_quantity = 11,
                        inventory_item_id = 11,
                        admin_graphql_api_id = "gid://shopify/ProductVariant/49540005036347",
                        image_id = ""
                    ),
                    Variant(
                        id = 49540005069115,
                        product_id = 9828914725179,
                        title = "5 / burgandy",
                        price = "29.00",
                        position = 2,
                        inventory_policy = "deny",
                        compare_at_price = "99.95",
                        option1 = "5",
                        option2 = "burgandy",
                        option3 = "",
                        created_at = "2024-10-01T03:32:07-04:00",
                        updated_at = "2024-10-01T03:35:12-04:00",
                        taxable = true,
                        barcode = "",
                        fulfillment_service = "manual",
                        grams = 0,
                        inventory_management = "shopify",
                        requires_shipping = true,
                        sku = "VN-01-burgandy-5",
                        weight = 0.0,
                        weight_unit = "kg",
                        inventory_item_id = 51587412164923,
                        inventory_quantity = 12,
                        old_inventory_quantity = 12,
                        admin_graphql_api_id = "gid://shopify/ProductVariant/49540005069115",
                        image_id = ""
                    ),
                    Variant(
                        id = 49540005101883,
                        product_id = 9828914725179,
                        title = "10 / burgandy",
                        price = "29.00",
                        position = 3,
                        inventory_policy = "deny",
                        compare_at_price = "99.95",
                        option1 = "10",
                        option2 = "burgandy",
                        option3 = "",
                        created_at = "2024-10-01T03:32:07-04:00",
                        updated_at = "2024-10-01T03:35:12-04:00",
                        taxable = true,
                        barcode = "",
                        fulfillment_service = "manual",
                        grams = 0,
                        inventory_management = "shopify",
                        requires_shipping = true,
                        sku = "VN-01-burgandy-10",
                        weight = 0.0,
                        weight_unit = "kg",
                        inventory_item_id = 51587412197691,
                        inventory_quantity = 1,
                        old_inventory_quantity = 1,
                        admin_graphql_api_id = "gid://shopify/ProductVariant/49540005101883",
                        image_id = ""
                    )
                ),
                options =
                emptyList(),
                images = listOf(
                    com.example.e_commerce_app.model.product.Image(
                        id = 48529993695547,
                        alt = "",
                        position = 1,
                        product_id = 9828914725179,
                        created_at = "2024-10-01T03:32:07-04:00",
                        updated_at = "2024-10-01T03:32:07-04:00",
                        admin_graphql_api_id = "gid://shopify/ProductImage/48529993695547",
                        width = 635,
                        height = 560,
                        src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/files/9f190cba7218c819c81566bca6298c6a.jpg?v=1727767927",
                        variant_ids = emptyList<Product>()
                    ),
                    com.example.e_commerce_app.model.product.Image(
                        id = 48529993728315,
                        alt = "",
                        position = 2,
                        product_id = 9828914725179,
                        created_at = "2024-10-01T03:32:07-04:00",
                        updated_at = "2024-10-01T03:32:07-04:00",
                        admin_graphql_api_id = "gid://shopify/ProductImage/48529993728315",
                        width = 635,
                        height = 560,
                        src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/files/57a6013d4b24210dd35456e5890e9a2e.jpg?v=1727767927",
                        variant_ids = emptyList()
                    )
                ),
                image = com.example.e_commerce_app.model.product.Image(
                    id = 48529993695547,
                    alt = "",
                    position = 1,
                    product_id = 9828914725179,
                    created_at = "2024-10-01T03:32:07-04:00",
                    updated_at = "2024-10-01T03:32:07-04:00",
                    admin_graphql_api_id = "gid://shopify/ProductImage/48529993695547",
                    width = 635,
                    height = 560,
                    src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/files/9f190cba7218c819c81566bca6298c6a.jpg?v=1727767927",
                    variant_ids = emptyList<Product>()
                )
            )
        )
    )


    val brandProducts = ProductResponse(
        listOf(
            Product(
                id = 9828917018939,
                title = "ADIDAS | CLASSIC BACKPACK",
                body_html = "This women's backpack has a glam look, thanks to a faux-leather build with an allover fur print. The front zip pocket keeps small things within reach, while an interior divider reins in potential chaos.",
                vendor = "ADIDAS",
                product_type = "ACCESSORIES",
                created_at = "2024-10-01T03:35:03-04:00",
                handle = "adidas-classic-backpack",
                updated_at = "2024-10-12T08:15:20-04:00",
                published_at = "2024-10-01T03:35:03-04:00",
                template_suffix = null,
                published_scope = "global",
                tags = "adidas, backpack, egnition-sample-data",
                status = "active",
                admin_graphql_api_id = "gid://shopify/Product/9828917018939",
                variants = listOf(
                    Variant(
                        id = 49540012212539,
                        product_id = 9828917018939,
                        title = "OS / black",
                        price = "70.00",
                        position = 1,
                        inventory_policy = "deny",
                        compare_at_price = "",
                        option1 = "OS",
                        option2 = "black",
                        option3 = "",
                        created_at = "2024-10-01T03:35:03-04:00",
                        updated_at = "2024-10-12T08:10:33-04:00",
                        taxable = true,
                        barcode = "",
                        fulfillment_service = "manual",
                        grams = 0,
                        inventory_management = "shopify",
                        requires_shipping = true,
                        sku = "AD-03-black-OS",
                        weight = 0.0,
                        weight_unit = "kg",
                        inventory_item_id = 51587419308347,
                        inventory_quantity = -13,
                        old_inventory_quantity = -13,
                        admin_graphql_api_id = "gid://shopify/ProductVariant/49540012212539",
                        image_id = ""
                    )
                ),
                options = listOf(
                    Option(
                        id = 12241727127867,
                        product_id = 9828917018939,
                        name = "Size",
                        position = 1,
                        values = listOf("OS")
                    ),
                    Option(
                        id = 12241727160635,
                        product_id = 9828917018939,
                        name = "Color",
                        position = 2,
                        values = listOf("black")
                    )
                ),
                images = listOf(
                    com.example.e_commerce_app.model.product.Image(
                        id = 48530036293947,
                        alt = "",
                        position = 1,
                        product_id = 9828917018939,
                        created_at = "2024-10-01T03:35:03-04:00",
                        updated_at = "2024-10-01T03:35:03-04:00",
                        admin_graphql_api_id = "gid://shopify/ProductImage/48530036293947",
                        width = 635,
                        height = 560,
                        src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/files/85cc58608bf138a50036bcfe86a3a362.jpg?v=1727768103",
                        variant_ids = emptyList()
                    ),
                    com.example.e_commerce_app.model.product.Image(
                        id = 48530036326715,
                        alt = "",
                        position = 2,
                        product_id = 9828917018939,
                        created_at = "2024-10-01T03:35:03-04:00",
                        updated_at = "2024-10-01T03:35:03-04:00",
                        admin_graphql_api_id = "gid://shopify/ProductImage/48530036326715",
                        width = 635,
                        height = 560,
                        src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/files/8a029d2035bfb80e473361dfc08449be.jpg?v=1727768103",
                        variant_ids = emptyList()
                    ),
                    com.example.e_commerce_app.model.product.Image(
                        id = 48530036359483,
                        alt = "",
                        position = 3,
                        product_id = 9828917018939,
                        created_at = "2024-10-01T03:35:03-04:00",
                        updated_at = "2024-10-01T03:35:03-04:00",
                        admin_graphql_api_id = "gid://shopify/ProductImage/48530036359483",
                        width = 635,
                        height = 560,
                        src = "https://cdn.shopify.com/s/files/1/0899/5212/5243/files/ad50775123e20f3d1af2bd07046b777d.jpg?v=1727768103",
                        variant_ids = emptyList()
                    )
                )
            )
        )
    )


    val customerOrders = CustomerOrders(
        listOf(
            Order(
                id = 6308399186235,
                admin_graphql_api_id = "gid://shopify/Order/6308399186235",
                app_id = 166078971905,
                browser_ip = "127.0.0.1",
                buyer_accepts_marketing = false,
                total_price = "200.0",
                created_at = "2024-10-12T08:11:43-04:00",
                location_id = 55555555,
                order_number = 5,
            )
        )
    )

}