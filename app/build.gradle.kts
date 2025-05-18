plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bitbloomadmin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bitbloomadmin"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES"
            )
        }
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
    implementation(platform(libs.firebase.bom))
    implementation(libs.gson)
    implementation(libs.androidx.gridlayout)


    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.google.firebase.firestore)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    ksp(libs.androidx.room.compiler)

    implementation("io.grpc:grpc-okhttp:1.56.1")


    // UI
    implementation(libs.lottie)
    implementation(libs.glide)
    implementation(libs.androidx.cardview)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ViewModel KTX (for viewModels() delegate)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // LiveData KTX (for LiveData observables)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Navigation
    val nav_version = "2.8.5"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")
    implementation ("com.airbnb.android:lottie:6.5.2")

    implementation(libs.circleimageview)
    implementation(libs.google.auth.library.oauth2.http)
    implementation(libs.ultra.ptr)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.picasso)
    implementation(libs.volley)
    implementation(libs.coinpayments.java)
    implementation(libs.okhttp)
    implementation(libs.glide)
    implementation(libs.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.firebase.storage)
    implementation(platform(libs.firebase.bom.v3273))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.config)
    implementation(libs.firebase.config.ktx)


    implementation(libs.firebase.firestore)
// Explicitly add gRPC core dependencies used by Firestore (fixes InternalGlobalInterceptors issue)
    implementation("io.grpc:grpc-okhttp:1.57.2")
    implementation("io.grpc:grpc-protobuf-lite:1.57.2")
    implementation("io.grpc:grpc-stub:1.57.2")
    implementation("io.grpc:grpc-core:1.57.2")
}