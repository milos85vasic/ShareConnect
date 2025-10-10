# Add project specific ProGuard rules here.

# Keep AI QA classes
-keep class com.shareconnect.qa.ai.** { *; }

# Keep test data models
-keep class com.shareconnect.qa.ai.models.** { *; }

# Keep serialization classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep YAML classes
-keep class org.yaml.snakeyaml.** { *; }

# Keep OkHttp and Retrofit
-keep class okhttp3.** { *; }
-keep class retrofit2.** { *; }

# Keep Android Test classes
-keep class androidx.test.** { *; }
-keep class androidx.test.uiautomator.** { *; }
-keep class androidx.test.espresso.** { *; }
