plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nexushub.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nexushub.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)

    // Activity + Lifecycle
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)

    // Navigation
    implementation(libs.navigation.compose)

    // Retrofit + OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.coroutines.android)

    // DataStore Preferences (token persistence)
    implementation(libs.datastore.preferences)

    // Coil (image loading)
    implementation(libs.coil.compose)

    // Core KTX
    implementation(libs.core.ktx)
}
