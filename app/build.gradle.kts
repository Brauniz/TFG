plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.changehome"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.changehome"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase BOM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
    // Firebase Authentication - AGREGADO
    implementation("com.google.firebase:firebase-auth")

    // Firebase Firestore (ya estaba)
    implementation("com.google.firebase:firebase-firestore:24.9.1")

    // Google Play Services Auth - AGREGADO (opcional para login con Google en el futuro)
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Tus dependencias existentes
    implementation(libs.glide)
    implementation(libs.firebase.storage)
    annotationProcessor(libs.glideCompiler)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}