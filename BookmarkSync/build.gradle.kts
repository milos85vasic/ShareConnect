plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.shareconnect.bookmarksync"
    compileSdk = 36
    defaultConfig {
        minSdk = 23
        targetSdk = 36
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":Asinka:asinka"))

    // Room
    val roomVersion = "2.8.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Gson for JSON serialization
    implementation("com.google.code.gson:gson:2.11.0")
}
