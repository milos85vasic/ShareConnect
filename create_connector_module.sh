#!/bin/bash

# Connector Module Generator Script

# Check if module name is provided
if [ $# -eq 0 ]; then
    echo "Usage: $0 <ModuleName>"
    exit 1
fi

MODULE_NAME="$1Connect"
MODULE_PACKAGE_NAME="com.shareconnect.${1,,}connect"
MODULE_PATH="/home/milosvasic/Projects/ShareConnect/Connectors/${MODULE_NAME}"

# Create directory structure
mkdir -p "${MODULE_PATH}/${MODULE_NAME}/src/main/kotlin/com/shareconnect/${1,,}connect/"{data/{api,database,model,repository},ui,sync,service,utils,viewmodels}
mkdir -p "${MODULE_PATH}/${MODULE_NAME}/src/main/res/"{layout,values,drawable,mipmap-hdpi,mipmap-mdpi,mipmap-xhdpi,mipmap-xxhdpi,mipmap-xxxhdpi}
mkdir -p "${MODULE_PATH}/${MODULE_NAME}/src/test/kotlin/com/shareconnect/${1,,}connect"
mkdir -p "${MODULE_PATH}/${MODULE_NAME}/src/androidTest/kotlin/com/shareconnect/${1,,}connect"

# Create build.gradle.kts
cat > "${MODULE_PATH}/${MODULE_NAME}/build.gradle.kts" << EOL
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("kotlinx-serialization")
}

android {
    namespace = "${MODULE_PACKAGE_NAME}"
    compileSdkVersion(36)

    defaultConfig {
        applicationId = "${MODULE_PACKAGE_NAME}"
        minSdkVersion(26)
        targetSdkVersion(36)
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    // Core dependencies
    implementation(project(":Toolkit:SecurityAccess"))
    implementation(project(":Toolkit:QRScanner"))
    
    // Android & Jetpack
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // Compose
    implementation("androidx.compose.ui:ui:1.6.1")
    implementation("androidx.compose.material3:material3:1.2.0")
    
    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Test Dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.1")
}
EOL

# Create basic classes
cat > "${MODULE_PATH}/${MODULE_NAME}/src/main/kotlin/com/shareconnect/${1,,}connect/data/model/${1}Data.kt" << EOL
package ${MODULE_PACKAGE_NAME}.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ${1}Data(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null
)
EOL

cat > "${MODULE_PATH}/${MODULE_NAME}/src/main/kotlin/com/shareconnect/${1,,}connect/ui/MainActivity.kt" << EOL
package ${MODULE_PACKAGE_NAME}.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shareconnect.toolkit.theme.AppTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    // Main UI for the connector
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}
EOL

# Create README
cat > "${MODULE_PATH}/${MODULE_NAME}/README.md" << EOL
# ${MODULE_NAME}

## Overview
${MODULE_NAME} is a ShareConnect connector for ${1} services, providing seamless integration and synchronization capabilities.

## Features
- Server connection management
- Sync capabilities
- Security access integration
- Material Design 3 UI

## Configuration
Add configuration in your app's settings or through the connection dialog.

## Usage
\`\`\`kotlin
// Example connection
val connection = ${1}Data(
    id = "main_server",
    name = "Main ${1} Server",
    host = "example.com",
    port = 8080
)
\`\`\`

## Dependencies
- Kotlin Coroutines
- Jetpack Compose
- Retrofit
- Room Database

## License
MIT License
EOL

echo "Connector module ${MODULE_NAME} created successfully!"
EOL