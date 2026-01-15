plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    // RETIRER LE "apply false" ICI car on veut l'activer dans l'app
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "fr.smeal"
    compileSdk = 36 // Mieux vaut mettre un entier simple ici si possible, sinon garde ton "version = release(36)"

    defaultConfig {
        applicationId = "fr.smeal"
        minSdk = 26 // CameraX demande souvent min 21, 24 est safe
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_1_8 // CameraX préfère Java 8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // AJOUT IMPORTANT POUR LE PROJET
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // --- UI Base ---
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // --- Navigation ---
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // --- Google Maps & Location ---
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // --- Firebase ---
    implementation(platform(libs.firebase.bom)) // Le BOM gère les versions
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // --- CameraX ---
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // --- Async Java Support (CameraX) ---
    implementation(libs.guava)
    implementation(libs.concurrent.futures)

    // --- Glide (Images) ---
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler) // "annotationProcessor" car Java

    // --- Tests ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}