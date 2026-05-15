plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.karunadavanyaa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.karunadavanyaa"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "2.0"
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
}

dependencies {
    // AppCompat — provides AppCompatActivity (fixes "Unresolved reference: AppCompatActivity")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Core Android KTX
    implementation("androidx.core:core-ktx:1.12.0")

    // Material Design (optional but recommended)
    implementation("com.google.android.material:material:1.11.0")
}
