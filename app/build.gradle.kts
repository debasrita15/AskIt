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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
//<<<<<<< HEAD
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore.ktx)
//    implementation(libs.androidx.navigation.runtime.android)
//    implementation(libs.androidx.navigation.compose.jvmstubs)
    implementation(libs.androidx.core.splashscreen)
//=======
//>>>>>>> origin/Askit
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
 profile
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("androidx.compose.ui:ui:1.6.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.material:material:1.6.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("androidx.compose.material:material:1.5.4")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")



//<<<<<<< HEAD

//=======
//    implementation("com.google.firebase:firebase-auth-ktx")
//    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
//    implementation("androidx.compose.ui:ui:1.6.1")
//    implementation("androidx.activity:activity-compose:1.8.0")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
//    implementation("androidx.compose.material:material:1.6.1")
//    implementation("androidx.navigation:navigation-compose:2.7.7")
//    implementation("androidx.activity:activity-compose:1.7.2")
//    implementation("androidx.compose.material3:material3:1.2.0")
    val nav_version = "2.9.1"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    implementation("io.coil-kt:coil-compose:2.4.0")
main
}
//apply(plugin = "com.google.gms.google-services")
//>>>>>>> origin/Askit
