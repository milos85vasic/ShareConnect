plugins {
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
}

buildscript {
    extra.apply {
        set("kotlin_version", "2.0.0")
        set("common_obfuscator_version", "1.0.0")

        set("shareconnect_version", "1.0.0")
        set("shareconnect_version_code", 4)
        set("shareconnect_application_name", "com.shareconnect")

        set("toolkit_version", "2.0.3")
        set("toolkit_context", ":Toolkit")
        set("toolkit_project_name", "ShareConnect")
    }

    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.9.1")
    }
}

allprojects {
    // Repositories are configured in settings.gradle.kts
    
    // Global dependency resolution strategy to avoid conflicts
    configurations.all {
        resolutionStrategy {
            // Force consistent protobuf versions and exclude conflicting ones
            force("com.google.protobuf:protobuf-java:3.25.1")
            force("com.google.protobuf:protobuf-javalite:3.25.1")
            
            // Force consistent BouncyCastle versions
            force("org.bouncycastle:bcprov-jdk18on:1.81")
            force("org.bouncycastle:bcpkix-jdk18on:1.81")
            
            // Force Spring versions to avoid commons-logging conflicts
            force("org.springframework:spring-core:5.3.21")
            force("org.springframework:spring-jcl:5.3.21")
            
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
                // Exclude commons-logging conflicts
                if (requested.group == "commons-logging" && requested.name == "commons-logging") {
                    useTarget("org.springframework:spring-jcl:5.3.21")
                }
            }
        }
    }
}

// Custom configuration for integration tests
apply(from = "build_config.gradle.kts")

// Add check task if it doesn't exist
tasks.register("check") {
    group = "verification"
    description = "Runs all checks"
}