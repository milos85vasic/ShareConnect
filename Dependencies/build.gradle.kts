plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.shareconnect.dependencies"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 36

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
}

// Define dependency versions as constants
object DependencyVersions {
    // Kotlin
    const val KOTLIN = "2.0.0"
    const val KOTLINX_COROUTINES = "1.10.2"
    const val KOTLINX_SERIALIZATION = "1.7.3"
    const val KOTLINX_DATETIME = "0.6.1"

    // AndroidX
    const val ANDROIDX_CORE = "1.15.0"
    const val ANDROIDX_APPCOMPAT = "1.7.0"
    const val ANDROIDX_LIFECYCLE = "2.9.4"
    const val ANDROIDX_ACTIVITY = "1.9.3"
    const val ANDROIDX_FRAGMENT = "1.8.1"
    const val ANDROIDX_ROOM = "2.6.1"
    const val ANDROIDX_BIOMETRIC = "1.1.0"
    const val ANDROIDX_STARTUP = "1.2.0"

    // Network
    const val OKHTTP = "4.12.0"
    const val RETROFIT = "2.11.0"
    const val GSON = "2.11.0"

    // gRPC
    const val GRPC = "1.57.1"
    const val GRPC_KOTLIN_STUB = "1.4.1"

    // Testing
    const val JUNIT = "4.13.2"
    const val MOCKITO = "5.8.0"
    const val MOCKITO_KOTLIN = "6.1.0"
    const val ROBOLECTRIC = "4.16"
    const val ESPRESSO = "3.7.0"
    const val ANDROIDX_TEST_CORE = "1.7.0"
    const val ANDROIDX_TEST_JUNIT = "1.3.0"
    const val ANDROIDX_TEST_RUNNER = "1.7.0"
    const val ANDROIDX_TEST_RULES = "1.7.0"
    const val HAMCREST = "3.0"

    // Other
    const val JSOUP = "1.18.3"
    const val FIREBASE_CRASHLYTICS = "19.1.0"
    const val FIREBASE_ANALYTICS = "22.1.0"

    // Security fixes
    const val JACKSON_DATABIND = "2.14.1"  // Fix CVE-2023-20862
    const val SPRING_CORE = "5.3.21"       // Fix CVE-2023-20861
    const val GUAVA = "31.1-jre"           // Fix CVE-2023-20863
}

dependencies {
    // Kotlin
    api("org.jetbrains.kotlin:kotlin-stdlib:${DependencyVersions.KOTLIN}")
    api("org.jetbrains.kotlin:kotlin-reflect:${DependencyVersions.KOTLIN}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${DependencyVersions.KOTLINX_COROUTINES}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.KOTLINX_COROUTINES}")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:${DependencyVersions.KOTLINX_SERIALIZATION}")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:${DependencyVersions.KOTLINX_SERIALIZATION}")
    api("org.jetbrains.kotlinx:kotlinx-datetime:${DependencyVersions.KOTLINX_DATETIME}")

    // AndroidX Core
    api("androidx.core:core:${DependencyVersions.ANDROIDX_CORE}")
    api("androidx.core:core-ktx:${DependencyVersions.ANDROIDX_CORE}")
    api("androidx.appcompat:appcompat:${DependencyVersions.ANDROIDX_APPCOMPAT}")
    api("androidx.lifecycle:lifecycle-runtime-ktx:${DependencyVersions.ANDROIDX_LIFECYCLE}")
    api("androidx.activity:activity:${DependencyVersions.ANDROIDX_ACTIVITY}")
    api("androidx.activity:activity-ktx:${DependencyVersions.ANDROIDX_ACTIVITY}")
    api("androidx.activity:activity-compose:${DependencyVersions.ANDROIDX_ACTIVITY}")
    api("androidx.fragment:fragment:${DependencyVersions.ANDROIDX_FRAGMENT}")
    api("androidx.fragment:fragment-ktx:${DependencyVersions.ANDROIDX_FRAGMENT}")

    // Room Database
    api("androidx.room:room-runtime:${DependencyVersions.ANDROIDX_ROOM}")
    api("androidx.room:room-ktx:${DependencyVersions.ANDROIDX_ROOM}")

    // AndroidX Startup
    api("androidx.startup:startup-runtime:${DependencyVersions.ANDROIDX_STARTUP}")

    // Network
    api("com.squareup.okhttp3:okhttp:${DependencyVersions.OKHTTP}")
    api("com.squareup.okhttp3:logging-interceptor:${DependencyVersions.OKHTTP}")
    api("com.squareup.okhttp3:mockwebserver:${DependencyVersions.OKHTTP}")
    api("com.squareup.retrofit2:retrofit:${DependencyVersions.RETROFIT}")
    api("com.squareup.retrofit2:converter-gson:${DependencyVersions.RETROFIT}")
    api("com.google.code.gson:gson:${DependencyVersions.GSON}")

    // gRPC
    api("io.grpc:grpc-api:${DependencyVersions.GRPC}")
    api("io.grpc:grpc-core:${DependencyVersions.GRPC}")
    api("io.grpc:grpc-kotlin-stub:${DependencyVersions.GRPC_KOTLIN_STUB}")
    api("io.grpc:grpc-netty-shaded:${DependencyVersions.GRPC}")
    api("io.grpc:grpc-protobuf:${DependencyVersions.GRPC}")
    api("io.grpc:grpc-stub:${DependencyVersions.GRPC}")

    // Biometric authentication
    api("androidx.biometric:biometric:${DependencyVersions.ANDROIDX_BIOMETRIC}")

    // HTML parsing
    api("org.jsoup:jsoup:${DependencyVersions.JSOUP}")

    // Firebase
    api("com.google.firebase:firebase-crashlytics:${DependencyVersions.FIREBASE_CRASHLYTICS}")
    api("com.google.firebase:firebase-analytics:${DependencyVersions.FIREBASE_ANALYTICS}")

    // Security fixes
    api("com.fasterxml.jackson.core:jackson-databind:${DependencyVersions.JACKSON_DATABIND}")
    api("org.springframework:spring-core:${DependencyVersions.SPRING_CORE}")
    api("com.google.guava:guava:${DependencyVersions.GUAVA}")

    // Testing dependencies (api so they're available to all modules)
    api("junit:junit:${DependencyVersions.JUNIT}")
    api("org.mockito:mockito-core:${DependencyVersions.MOCKITO}")
    api("org.mockito.kotlin:mockito-kotlin:${DependencyVersions.MOCKITO_KOTLIN}")
    api("org.robolectric:robolectric:${DependencyVersions.ROBOLECTRIC}")
    api("androidx.test:core:${DependencyVersions.ANDROIDX_TEST_CORE}")
    api("androidx.test.ext:junit:${DependencyVersions.ANDROIDX_TEST_JUNIT}")
    api("androidx.test:runner:${DependencyVersions.ANDROIDX_TEST_RUNNER}")
    api("androidx.test:rules:${DependencyVersions.ANDROIDX_TEST_RULES}")
    api("androidx.arch.core:core-testing:2.2.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-test:${DependencyVersions.KOTLINX_COROUTINES}")
    api("androidx.room:room-testing:${DependencyVersions.ANDROIDX_ROOM}")
    debugApi("androidx.fragment:fragment-testing:${DependencyVersions.ANDROIDX_FRAGMENT}")
    api("org.hamcrest:hamcrest-core:${DependencyVersions.HAMCREST}")
    api("org.hamcrest:hamcrest-library:${DependencyVersions.HAMCREST}")

    // Instrumentation testing
    api("androidx.test.espresso:espresso-core:${DependencyVersions.ESPRESSO}")
    api("androidx.test.espresso:espresso-intents:${DependencyVersions.ESPRESSO}")
    api("androidx.test.espresso:espresso-contrib:${DependencyVersions.ESPRESSO}")
    api("androidx.test.espresso:espresso-accessibility:${DependencyVersions.ESPRESSO}")
    api("androidx.test.espresso:espresso-web:${DependencyVersions.ESPRESSO}")
    api("androidx.test.espresso.idling:idling-concurrent:${DependencyVersions.ESPRESSO}")
    api("androidx.test.uiautomator:uiautomator:2.3.0")
    api("org.mockito:mockito-android:${DependencyVersions.MOCKITO}")
}

// Force consistent versions to avoid conflicts
configurations.all {
    resolutionStrategy {
        // Force Kotlin versions
        force("org.jetbrains.kotlin:kotlin-stdlib:${DependencyVersions.KOTLIN}")
        force("org.jetbrains.kotlin:kotlin-reflect:${DependencyVersions.KOTLIN}")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${DependencyVersions.KOTLIN}")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${DependencyVersions.KOTLIN}")
        force("org.jetbrains.kotlin:kotlin-stdlib-common:${DependencyVersions.KOTLIN}")

        // Force okhttp versions
        force("com.squareup.okhttp3:okhttp:${DependencyVersions.OKHTTP}")
        force("com.squareup.okhttp3:logging-interceptor:${DependencyVersions.OKHTTP}")
        force("com.squareup.okhttp3:mockwebserver:${DependencyVersions.OKHTTP}")

        // Force kotlinx-serialization versions
        force("org.jetbrains.kotlinx:kotlinx-serialization-core:${DependencyVersions.KOTLINX_SERIALIZATION}")
        force("org.jetbrains.kotlinx:kotlinx-serialization-json:${DependencyVersions.KOTLINX_SERIALIZATION}")
        force("org.jetbrains.kotlinx:kotlinx-datetime:${DependencyVersions.KOTLINX_DATETIME}")

        // Force AndroidX versions
        force("androidx.core:core:${DependencyVersions.ANDROIDX_CORE}")
        force("androidx.core:core-ktx:${DependencyVersions.ANDROIDX_CORE}")
        force("androidx.activity:activity:${DependencyVersions.ANDROIDX_ACTIVITY}")
        force("androidx.activity:activity-ktx:${DependencyVersions.ANDROIDX_ACTIVITY}")
        force("androidx.activity:activity-compose:${DependencyVersions.ANDROIDX_ACTIVITY}")
        force("androidx.startup:startup-runtime:${DependencyVersions.ANDROIDX_STARTUP}")

        // Security fixes
        force("com.fasterxml.jackson.core:jackson-databind:${DependencyVersions.JACKSON_DATABIND}")
        force("org.springframework:spring-core:${DependencyVersions.SPRING_CORE}")
        force("com.google.guava:guava:${DependencyVersions.GUAVA}")
        force("org.jsoup:jsoup:${DependencyVersions.JSOUP}")

        // Resolve protobuf conflicts - prefer protobuf-java over protobuf-lite
        force("com.google.protobuf:protobuf-java:3.25.1")
        force("com.google.protobuf:protobuf-javalite:3.25.1")
        
        // Resolve BouncyCastle conflicts - prefer newer version
        force("org.bouncycastle:bcprov-jdk18on:1.81")
        force("org.bouncycastle:bcpkix-jdk18on:1.81")
        
        // Exclude conflicting protobuf and BouncyCastle versions
        eachDependency {
            if (requested.group == "com.google.protobuf" && requested.name == "protobuf-lite") {
                useTarget("com.google.protobuf:protobuf-java:3.25.1")
            }
            if (requested.group == "org.bouncycastle" && requested.name == "bcprov-jdk15on") {
                useTarget("org.bouncycastle:bcprov-jdk18on:1.81")
            }
            if (requested.group == "org.bouncycastle" && requested.name == "bcpkix-jdk15on") {
                useTarget("org.bouncycastle:bcpkix-jdk18on:1.81")
            }
        }
    }
}