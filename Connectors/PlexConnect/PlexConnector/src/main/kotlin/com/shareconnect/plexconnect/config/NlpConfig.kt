/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.plexconnect.config

/**
 * Configuration for NLP models and AI recommendation settings
 */
object NlpConfig {
    
    // Model file paths (relative to assets directory)
    const val MULTILINGUAL_BERT_MODEL = "models/multilingual_bert.tflite"
    const val ENGLISH_BERT_MODEL = "models/english_bert.tflite"
    const val CHINESE_BERT_MODEL = "models/chinese_bert.tflite"
    const val JAPANESE_BERT_MODEL = "models/japanese_bert.tflite"
    const val KOREAN_BERT_MODEL = "models/korean_bert.tflite"
    const val ARABIC_BERT_MODEL = "models/arabic_bert.tflite"
    const val HINDI_BERT_MODEL = "models/hindi_bert.tflite"
    
    // Embedding dimensions
    const val EMBEDDING_DIMENSION = 768
    const val CROSS_MODAL_DIMENSION = 512
    
    // Similarity thresholds
    const val DEFAULT_SIMILARITY_THRESHOLD = 0.7
    const val HIGH_SIMILARITY_THRESHOLD = 0.8
    const val MODERATE_SIMILARITY_THRESHOLD = 0.6
    const val LOW_SIMILARITY_THRESHOLD = 0.5
    
    // Recommendation limits
    const val DEFAULT_MAX_RECOMMENDATIONS = 20
    const val MIN_RECOMMENDATIONS = 5
    const val MAX_RECOMMENDATIONS = 50
    
    // Processing limits
    const val MAX_TEXT_LENGTH = 512
    const val MAX_BATCH_SIZE = 32
    const val PROCESSING_TIMEOUT_MS = 5000L
    
    // Caching settings
    const val EMBEDDING_CACHE_SIZE = 1000
    const val EMBEDDING_EXPIRY_HOURS = 24L
    const val METADATA_CACHE_SIZE = 500
    
    // Performance settings
    const val MAX_CONCURRENT_EMBEDDINGS = 4
    const val SIMILARITY_COMPUTE_BATCH_SIZE = 100
    
    // Model settings
    const val MODEL_VERSION = "1.0.0"
    const val FALLBACK_MODEL = "models/fallback_bert.tflite"
    
    // Language detection
    val SUPPORTED_LANGUAGES = setOf("en", "zh", "ja", "ko", "ar", "hi", "es", "fr", "de", "ru")
    const val DEFAULT_LANGUAGE = "en"
    const val LANGUAGE_CONFIDENCE_THRESHOLD = 0.8
    
    // Error handling
    const val MAX_RETRIES = 3
    const val RETRY_DELAY_MS = 1000L
    const val CIRCUIT_BREAKER_THRESHOLD = 5
    const val CIRCUIT_BREAKER_TIMEOUT_MS = 30000L
    
    // Monitoring and logging
    const val ENABLE_PERFORMANCE_LOGGING = true
    const val EMBEDDING_GENERATION_LOG_INTERVAL = 100
    const val SIMILARITY_COMPUTE_LOG_INTERVAL = 500
    
    // Feature flags
    const val ENABLE_CROSS_MODAL_ANALYSIS = true
    const val ENABLE_CROSS_LINGUAL_RECOMMENDATIONS = true
    const val ENABLE_SEMANTIC_DRIFT_COMPENSATION = true
    const val ENABLE_CONTEXT_ENHANCEMENT = true
    
    // Quality thresholds
    const val MIN_EMBEDDING_QUALITY_SCORE = 0.5f
    const val PREFERRED_EMBEDDING_QUALITY_SCORE = 0.8f
    
    // Batch processing
    const val RECOMMENDATION_BATCH_SIZE = 10
    const val PRECOMPUTE_EMBEDDINGS_BATCH_SIZE = 50
    
    // User preferences
    const val PREF_ENABLE_AI_RECOMMENDATIONS = "enable_ai_recommendations"
    const val PREF_SIMILARITY_THRESHOLD = "similarity_threshold"
    const val PREF_RECOMMENDATION_COUNT = "recommendation_count"
    const val PREF_LANGUAGE_PREFERENCE = "language_preference"
    const val PREF_ENABLE_CROSS_LINGUAL = "enable_cross_lingual"
    
    // API endpoints (for future cloud-based processing)
    const val CLOUD_EMBEDDING_API_ENDPOINT = "https://api.plexconnect.ai/v1/embeddings"
    const val CLOUD_RECOMMENDATION_API_ENDPOINT = "https://api.plexconnect.ai/v1/recommendations"
    
    // Rate limiting
    const val MAX_API_REQUESTS_PER_MINUTE = 100
    const val MAX_EMBEDDING_REQUESTS_PER_HOUR = 1000
    
    // Storage
    const val EMBEDDING_DATABASE_NAME = "plex_embeddings.db"
    const val EMBEDDING_BACKUP_INTERVAL_HOURS = 6
    
    /**
     * Get the appropriate model file for a language
     */
    fun getModelForLanguage(language: String): String {
        return when (language.lowercase()) {
            "zh", "zh-cn", "zh-tw" -> CHINESE_BERT_MODEL
            "ja", "jp" -> JAPANESE_BERT_MODEL
            "ko", "kr" -> KOREAN_BERT_MODEL
            "ar" -> ARABIC_BERT_MODEL
            "hi", "in" -> HINDI_BERT_MODEL
            else -> MULTILINGUAL_BERT_MODEL
        }
    }
    
    /**
     * Check if a language is supported for advanced processing
     */
    fun isLanguageSupported(language: String): Boolean {
        return SUPPORTED_LANGUAGES.contains(language.lowercase())
    }
    
    /**
     * Get similarity threshold for recommendation type
     */
    fun getSimilarityThreshold(type: SimilarityType): Double {
        return when (type) {
            SimilarityType.HIGH -> HIGH_SIMILARITY_THRESHOLD
            SimilarityType.MODERATE -> MODERATE_SIMILARITY_THRESHOLD
            SimilarityType.LOW -> LOW_SIMILARITY_THRESHOLD
            SimilarityType.DEFAULT -> DEFAULT_SIMILARITY_THRESHOLD
        }
    }
    
    enum class SimilarityType {
        HIGH,
        MODERATE,
        LOW,
        DEFAULT
    }
}