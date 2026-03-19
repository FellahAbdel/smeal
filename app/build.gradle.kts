import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "fr.smeal"
    compileSdk = 36 // Mieux vaut mettre un entier simple ici si possible, sinon garde ton "version = release(36)"

    defaultConfig {
        applicationId = "fr.smeal"
        minSdk = 26 // CameraX demande souvent min 21, 24 est safe
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Chargement des propriétés depuis local.properties
        val keystoreFile = project.rootProject.file("local.properties")
        val properties = Properties()
        if (keystoreFile.exists()) {
            properties.load(keystoreFile.inputStream())
        }

        // Récupération de la clé API
        val apiKey = properties.getProperty("API_KEY") ?: ""

        // Injection pour le code Java (BuildConfig.API_KEY)
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        
        // Injection pour le Manifest (${API_KEY})
        manifestPlaceholders["API_KEY"] = apiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // --- UI Base ---
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.shimmer)
    implementation(libs.lottie)

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
    implementation(libs.firebase.auth)

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
    annotationProcessor(libs.glide.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
