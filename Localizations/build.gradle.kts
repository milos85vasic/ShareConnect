plugins {
    id("com.android.library")
}

android {
    namespace = "com.shareconnect.localizations"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        targetSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
