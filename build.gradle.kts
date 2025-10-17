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

        maven { url = uri("https://repo.huaweicloud.com/repository/maven/") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/jetbrains") }
        maven { url = uri("https://mirrors.ustc.edu.cn/jetbrains/") }
        maven { url = uri("https://mirrors.tuna.tsinghua.edu.cn/jetbrains/") }
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
        google()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
    }
}

allprojects {
    // Repositories are configured in settings.gradle.kts

    // Force Kotlin version to avoid conflicts
     configurations.all {
         resolutionStrategy {
             force("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
             force("org.jetbrains.kotlin:kotlin-reflect:2.0.0")
             force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.0")
             force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")
             force("org.jetbrains.kotlin:kotlin-stdlib-common:2.0.0")
             // Force okhttp versions to avoid Kotlin version conflicts
             force("com.squareup.okhttp3:okhttp:4.12.0")
             force("com.squareup.okhttp3:logging-interceptor:4.12.0")
             force("com.squareup.okhttp3:mockwebserver:4.12.0")
             // Force kotlinx-serialization versions to avoid Kotlin version conflicts
             force("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")
             force("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
             force("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
             // Force AndroidX versions compatible with AGP 8.7.3
             force("androidx.core:core:1.15.0")
             force("androidx.core:core-ktx:1.15.0")
             force("androidx.activity:activity:1.9.3")
             force("androidx.activity:activity-ktx:1.9.3")
             force("androidx.activity:activity-compose:1.9.3")
         }
     }
}