import com.android.build.gradle.BaseExtension

// Shared Android configuration for all modules
configure<BaseExtension> {
    compileSdkVersion(36)
    
    defaultConfig {
        minSdkVersion(26)
        targetSdkVersion(36)
        
        // Standard version configurations
        versionCode = project.findProperty("shareconnect_version_code") as? Int ?: 1
        versionName = project.findProperty("shareconnect_version") as? String ?: "1.0.0"
        
        // Standardize test instrumentation
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    // Common lint options
    lintOptions {
        isCheckReleaseBuilds = true
        isAbortOnError = false
        disable("MissingTranslation")
    }
}

// Common Android plugin configuration
plugins.withId("com.android.application") {
    configure<BaseExtension> {
        packagingOptions {
            resources.excludes.add("META-INF/DEPENDENCIES")
            resources.excludes.add("META-INF/LICENSE")
            resources.excludes.add("META-INF/NOTICE")
        }
    }
}

// Shared test configuration
dependencies {
    add("testImplementation", "junit:junit:4.13.2")
    add("androidTestImplementation", "androidx.test.ext:junit:1.1.5")
    add("androidTestImplementation", "androidx.test.espresso:espresso-core:3.5.1")
}