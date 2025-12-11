plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.shareconnect.languagesync"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        testOptions.targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    // Centralized dependencies
    implementation(project(":Dependencies"))

    // Room Database KSP compiler
    ksp("androidx.room:room-compiler:2.6.1")

    // Asinka for syncing
    implementation(project(":Asinka:asinka"))
    testImplementation(project(":Asinka:asinka"))

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    
    // Integration test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
}

// Integration tests configuration
tasks.register<Test>("integrationTest") {
    group = "verification"
    description = "Runs integration tests"
    useJUnitPlatform {
        includeTags("integration")
    }
}

// Ensure integrationTest task is added to the check lifecycle
tasks.named("check") {
    dependsOn("integrationTest")
}