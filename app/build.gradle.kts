plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//<<<<<<< HEAD
    alias(libs.plugins.google.gms.google.services)
//=======
//>>>>>>> origin/Askit
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.example.askit"
//<<<<<<< HEAD
//    compileSdk = 35
//=======
    compileSdk = 36
//>>>>>>> origin/Askit

    defaultConfig {
        applicationId = "com.example.askit"
        minSdk = 24
//<<<<<<< HEAD
//        targetSdk = 35
//=======
        targetSdk = 36
//>>>>>>> origin/Askit
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)

        // Firebase (Using BOM)
        implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
        implementation("com.google.firebase:firebase-auth-ktx")
        implementation("com.google.firebase:firebase-firestore-ktx")

        implementation("androidx.core:core-splashscreen:1.0.1")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
        implementation("androidx.navigation:navigation-compose:2.7.7")
        implementation("androidx.compose.material:material:1.6.1")
        implementation("androidx.compose.material:material-icons-extended:1.6.1")
        implementation("androidx.compose.material3:material3:1.2.0")
        implementation("io.coil-kt:coil-compose:2.4.0")

        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)
    }

}
