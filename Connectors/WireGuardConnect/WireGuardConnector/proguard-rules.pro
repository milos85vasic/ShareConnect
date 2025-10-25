# Add project specific ProGuard rules here.

# Keep WireGuard models for serialization
-keep class com.shareconnect.wireguardconnect.data.models.** { *; }

# Keep ZXing classes for QR code functionality
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# Keep Ktor classes
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Keep Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep SecurityAccess classes
-keep class digital.vasic.security.access.** { *; }

# Keep Asinka classes
-keep class com.shareconnect.asinka.** { *; }

# Keep Sync Manager classes
-keep class com.shareconnect.*sync.** { *; }
