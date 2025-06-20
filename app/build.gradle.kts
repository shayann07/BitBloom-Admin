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

    // Core AndroidX + UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.cardview)

    // Firebase BOM (manages all Firebase versions)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.config)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    // ViewModel + LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Other Libraries
    implementation(libs.lottie)
    implementation(libs.glide)
    implementation(libs.circleimageview)
    implementation(libs.google.auth.library.oauth2.http)
    implementation(libs.ultra.ptr)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.picasso)
    implementation(libs.volley)
    implementation(libs.coinpayments.java)
    implementation(libs.core)
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Additional Navigation (explicit version, optional)
    val nav_version = "2.8.5"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // ✅ gRPC Dependencies for Firestore (fixes InternalGlobalInterceptors issue)
    implementation(libs.grpc.okhttp)
    implementation(libs.grpc.core)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.protobuf.lite)
   implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation( "androidx.work:work-runtime-ktx:2.10.1")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")

}