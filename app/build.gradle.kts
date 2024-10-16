plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    alias(libs.plugins.google.gms.google.services)
}

android {


    namespace = "com.example.e_commerce_app"
    compileSdk = 34

    android.buildFeatures.buildConfig = true


    defaultConfig {
        applicationId = "com.example.e_commerce_app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


//
//        // Load properties from local.properties
//        val accessToken: String = project.findProperty("ACCESS_TOKEN") as? String ?: ""
//        val baseUrl: String = project.findProperty("BASE_URL") as? String ?: ""
//
//        // Set the values to BuildConfig
//        buildConfigField("String", "ACCESS_TOKEN", "\"$accessToken\"")
//        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
//

    }





    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }


    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // Retrofit and Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.gson)

    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Glide
    implementation(libs.glide)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Lifecycle components
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Circle Image
    implementation(libs.circleimageview)

    // Lottie Animation
    implementation(libs.lottie)

    // Google Play Services - Location
    implementation(libs.play.services.location)

    // AndroidX Core KTX
    implementation(libs.androidx.core.ktx.v160)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.hamcrest.all)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.junit.ktx)
    testImplementation(libs.core.ktx)

    // Android Instrumentation Testing
    androidTestImplementation(libs.androidx.espresso.core.v340)
    androidTestImplementation(libs.androidx.core.testing)

    // Timber
    implementation(libs.timber)

    // Hamcrest
    testImplementation(libs.hamcrest)
    testImplementation(libs.hamcrest.library)
    androidTestImplementation(libs.hamcrest)
    androidTestImplementation(libs.hamcrest.library)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
    androidTestImplementation(libs.jetbrains.kotlinx.coroutines.test)

    //Google Maps
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)


    //Google Material Design
    implementation("com.google.android.material:material:1.8.0")


    implementation("com.google.android.gms:play-services-auth:20.4.0")

    //view Pager
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Pager Dots Indicator
    implementation("com.tbuonomo:dotsindicator:4.2")


    // AndroidX and Robolectric
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("org.robolectric:robolectric:4.8")

    // Paypal
    implementation("com.paypal.android:paypal-web-payments:1.5.0")
    //implementation("com.paypal.sdk:paypal-android-sdk:2.16.0")


    //mock
    testImplementation ("org.mockito:mockito-core:5.2.0")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:5.0.0")





}