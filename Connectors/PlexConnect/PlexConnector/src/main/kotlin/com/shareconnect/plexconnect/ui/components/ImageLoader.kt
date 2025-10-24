package com.shareconnect.plexconnect.ui.components

import android.content.Context
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger

/**
 * Optimized ImageLoader for PlexConnect with enhanced caching and memory management
 */
@OptIn(ExperimentalCoilApi::class)
object PlexImageLoader {

    private var imageLoader: ImageLoader? = null

    fun getImageLoader(context: Context): ImageLoader {
        return imageLoader ?: createImageLoader(context).also { imageLoader = it }
    }

    private fun createImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            // Memory cache configuration
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // Use 25% of available memory
                    .build()
            }
            // Disk cache configuration
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02) // Use 2% of available disk space
                    .build()
            }
            // Cache policies
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            // Performance optimizations
            .allowHardware(true) // Enable hardware bitmaps when possible
            .allowRgb565(true) // Allow RGB565 for better memory usage
            .build()
    }

    fun clearCache(context: Context) {
        imageLoader?.let { loader ->
            loader.memoryCache?.clear()
            loader.diskCache?.clear()
        }
    }
}