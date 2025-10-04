plugins {
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
}

buildscript {

    extra.apply {
        set("kotlin_version", "2.1.0")
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
        classpath("com.android.tools.build:gradle:8.9.2")
    }
}

allprojects {
    // Repositories are configured in settings.gradle.kts
}