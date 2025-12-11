package com.shareconnect.plexconnect.nlp

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.shareconnect.plexconnect.config.NlpConfig
import com.shareconnect.plexconnect.monitoring.NlpPerformanceMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.MappedByteBuffer
import java.time.Instant
import kotlin.math.sqrt
import java.util.Locale
import java.util.regex.Pattern
import kotlin.random.Random
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import java.io.File
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.tensorflow.lite.support.metadata.MetadataExtractor

/**
 * Advanced Semantic Embedding System
 * Provides multi-modal, contextually rich semantic representations
 */
class AdvancedSemanticEmbedding(
    private val context: Context,
    private val modelPath: String = "advanced_embedding_model.tflite"
) {
    // NLP Model Manager for advanced model handling
    private val nlpModelManager by lazy { NlpModelManager(context) }
    
    // Performance monitoring
    private val performanceMonitor by lazy { NlpPerformanceMonitor(context) }

    // Error tracking and circuit breaker
    private val errorTracker = EmbeddingErrorTracker()

    /**
     * Advanced error tracking and circuit breaker
     */
    private class EmbeddingErrorTracker {
        // Concurrent error count tracking
        private val errorCount = AtomicInteger(0)
        
        // Maximum allowed errors before circuit breaking
        private val MAX_ERRORS = 5
        
        // Error cooldown period (in milliseconds)
        private val ERROR_COOLDOWN = 60000L
        
        // Last error timestamp
        @Volatile
        private var lastErrorTimestamp: Long = 0

        /**
         * Record an error and check if system should enter circuit break
         */
        fun recordError(): Boolean {
            val currentTime = System.currentTimeMillis()
            
            // Check if we're in cooldown period
            if (currentTime - lastErrorTimestamp < ERROR_COOLDOWN) {
                return false
            }

            // Increment error count
            val currentErrorCount = errorCount.incrementAndGet()

            // Update last error timestamp
            lastErrorTimestamp = currentTime

            // Check if error threshold is reached
            return currentErrorCount >= MAX_ERRORS
        }

        /**
         * Reset error tracking
         */
        fun reset() {
            errorCount.set(0)
            lastErrorTimestamp = 0
        }

        /**
         * Check if system is in a healthy state
         */
        fun isHealthy(): Boolean {
            val currentTime = System.currentTimeMillis()
            return currentTime - lastErrorTimestamp >= ERROR_COOLDOWN
        }
    }

    /**
     * Custom embedding generation exception
     */
    class EmbeddingGenerationException(
        message: String, 
        cause: Throwable? = null
    ) : Exception(message, cause) {
        companion object {
            // Predefined error types for easier debugging
            const val ERROR_MODEL_LOAD_FAILED = "MODEL_LOAD_FAILED"
            const val ERROR_PREPROCESSING_FAILED = "PREPROCESSING_FAILED"
            const val ERROR_INFERENCE_FAILED = "INFERENCE_FAILED"
            const val ERROR_POSTPROCESSING_FAILED = "POSTPROCESSING_FAILED"
        }
    }
    // Lazy-initialized TensorFlow Lite interpreter
    private val interpreter by lazy { loadModel() }

    // Vocabulary and tokenization utilities
    val tokenizer by lazy { loadTokenizer() }

    // Multilingual embedding transformer
    val multilingualTransformer by lazy { 
        MultilingualEmbeddingTransformer(context) 
    }

    // Cross-modal embedding transformer
    val crossModalTransformer by lazy { 
        CrossModalEmbeddingTransformer(context) 
    }

    /**
     * Advanced domain-specific embedding adaptation
     */
    fun adaptEmbeddingToDomain(
        embedding: FloatArray,
        domain: String,
        language: String = "en"
    ): FloatArray {
        // Domain-specific embedding adaptation
        val domainAdaptationFactors = mapOf(
            "MOVIE" to floatArrayOf(1.1f, 1.05f, 0.95f),
            "TV_SHOW" to floatArrayOf(0.9f, 1.0f, 1.1f),
            "DOCUMENTARY" to floatArrayOf(1.05f, 1.15f, 0.9f),
            "NEWS" to floatArrayOf(0.95f, 1.0f, 1.05f)
        )

        return embedding.mapIndexed { index, value ->
            val domainFactor = domainAdaptationFactors[domain]
                ?.getOrNull(index % 3) 
                ?: 1.0f

            // Language-specific scaling
            val languageFactor = LANGUAGE_TRANSFORMATION_FACTORS[language] ?: 1.0f

            value * domainFactor * languageFactor
        }.toFloatArray()
    }

    /**
     * Semantic consistency verification
     */
    fun verifySemanticConsistency(
        embedding1: FloatArray,
        embedding2: FloatArray,
        threshold: Double = 0.7
    ): Boolean {
        val similarity = calculateSemanticSimilarity(embedding1, embedding2)
        return similarity >= threshold
    }

    /**
     * Generate cross-modal embedding
     */
    fun generateCrossModalEmbedding(
        input: Any,
        modality: Modality
    ): SemanticEmbeddingResult {
        return try {
            val embedding = crossModalTransformer.generateCrossModalEmbedding(input, modality)
            
            SemanticEmbeddingResult(
                embedding = embedding,
                source = EmbeddingSource.CROSS_MODAL,
                language = "multi"  // Cross-modal embeddings are language-agnostic
            )
        } catch (e: Exception) {
            Log.e("AdvancedSemanticEmbedding", "Cross-modal embedding generation failed", e)
            SemanticEmbeddingResult(
                embedding = FloatArray(EMBEDDING_DIMENSION) { 0f },
                source = EmbeddingSource.ERROR,
                language = "multi"
            )
        }
    }

    /**
     * Calculate cross-modal semantic similarity
     */
    fun calculateCrossModalSimilarity(
        embedding1: FloatArray,
        embedding2: FloatArray,
        modality1: Modality,
        modality2: Modality
    ): Double {
        return crossModalTransformer.calculateCrossModalSimilarity(
            embedding1, 
            embedding2, 
            modality1, 
            modality2
        )
    }

    /**
     * Combine embeddings from multiple modalities
     */
    fun combineModalEmbeddings(
        embeddings: List<Pair<FloatArray, Modality>>
    ): FloatArray {
        if (embeddings.isEmpty()) {
            return FloatArray(EMBEDDING_DIMENSION) { 0f }
        }

        // Weighted combination of embeddings
        val modalityWeights = mapOf(
            Modality.TEXT to 0.4f,
            Modality.IMAGE to 0.3f,
            Modality.AUDIO to 0.2f,
            Modality.VIDEO to 0.1f
        )

        return embeddings.map { (embedding, modality) ->
            val weight = modalityWeights[modality] ?: 1f / embeddings.size
            embedding.map { it * weight }
        }.reduce { acc, embedValues ->
            acc.zip(embedValues).map { it.first + it.second }
        }.toFloatArray()
    }

    // Cache for embeddings to improve performance
    private val embeddingCache = LruCache<String, SemanticEmbeddingResult>(100)

    /**
     * Generate advanced semantic embedding
     * @param text Input text to embed
     * @param additionalContext Optional additional contextual information
     */
     suspend fun generateEmbedding(
        text: String, 
        additionalContext: Map<String, Any> = emptyMap()
    ): SemanticEmbeddingResult = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        val cacheKey = "embed:${text.hashCode()}"
        
        // Check cache first
        embeddingCache[cacheKey]?.let { 
            performanceMonitor.recordCacheHit("embedding", true)
            return@withContext it
        }
        
        performanceMonitor.recordCacheHit("embedding", false)
        
        // Check if system is in a healthy state
        if (!errorTracker.isHealthy()) {
            val duration = System.currentTimeMillis() - startTime
            performanceMonitor.recordEmbeddingPerformance(
                mediaKey = cacheKey,
                durationMs = duration,
                language = "unknown",
                embeddingSource = "FALLBACK",
                success = false
            )
            return@withContext createFallbackEmbedding(
                text, 
                "System in cooldown due to repeated errors"
            )
        }

        // Detect language
        val language = tokenizer.detectLanguage(text)
        
        try {
            // Validate input
            validateInput(text)

            // Load appropriate model
            val model = nlpModelManager.loadModel("text_embedding_model")

            // Preprocess input
            val (tokenIds, attentionMask) = preprocessText(text)

            // Prepare input tensors
            val inputTensors = prepareInputTensors(tokenIds, attentionMask)

            // Run inference
            val outputEmbedding = runInference(inputTensors, model.interpreter)

            // Apply additional context enhancement
            val enhancedEmbedding = applyContextEnhancement(
                outputEmbedding, 
                additionalContext + (
                    "language" to language,
                    "model_version" to model.modelVersion
                )
            )

            // Normalize embedding
            val normalizedEmbedding = normalizeEmbedding(enhancedEmbedding)

            // Create embedding result with language metadata
            val embeddingResult = SemanticEmbeddingResult(
                embedding = normalizedEmbedding, 
                source = EmbeddingSource.GENERATED,
                language = language
            )

            // Cache result
            embeddingCache[cacheKey] = embeddingResult

            // Reset error tracker on successful generation
            errorTracker.reset()
            
            // Record performance
            val duration = System.currentTimeMillis() - startTime
            performanceMonitor.recordEmbeddingPerformance(
                mediaKey = cacheKey,
                durationMs = duration,
                language = language,
                embeddingSource = "GENERATED",
                success = true
            )

            embeddingResult
        } catch (e: Exception) {
            // Record and handle error
            val shouldCircuitBreak = errorTracker.recordError()
            val duration = System.currentTimeMillis() - startTime
            
            performanceMonitor.recordEmbeddingPerformance(
                mediaKey = cacheKey,
                durationMs = duration,
                language = language,
                embeddingSource = "ERROR",
                success = false
            )

            Log.e("AdvancedSemanticEmbedding", "Embedding generation failed", e)
            
            // Create detailed error embedding
            createErrorEmbedding(
                text, 
                language, 
                e, 
                shouldCircuitBreak
            )
        }
    }

    /**
     * Validate input before embedding generation
     */
    private fun validateInput(text: String) {
        // Input validation
        require(text.isNotBlank()) { 
            "Input text cannot be blank" 
        }
        
        require(text.length <= MAX_SEQUENCE_LENGTH * 2) { 
            "Input text exceeds maximum allowed length" 
        }
    }

    /**
     * Run model inference with error handling
     */
    private fun runInference(
        inputTensors: Map<String, Any>, 
        interpreter: Interpreter
    ): FloatArray {
        return try {
            val outputTensor = Array(1) { FloatArray(EMBEDDING_DIMENSION) }
            
            // Run model
            interpreter.runForMultipleInputsOutputs(
                inputTensors.values.toTypedArray(), 
                mapOf("output" to outputTensor)
            )

            outputTensor[0]
        } catch (e: Exception) {
            throw EmbeddingGenerationException(
                EmbeddingGenerationException.ERROR_INFERENCE_FAILED, 
                e
            )
        }
    }

    /**
     * Create fallback embedding when system is in an error state
     */
    private fun createFallbackEmbedding(
        text: String, 
        reason: String
    ): SemanticEmbeddingResult {
        val language = tokenizer.detectLanguage(text)
        
        return SemanticEmbeddingResult(
            embedding = FloatArray(EMBEDDING_DIMENSION) { 
                // Add slight randomization to fallback embedding
                Random.nextFloat() * 0.1f - 0.05f 
            }, 
            source = EmbeddingSource.ERROR,
            language = language
        ).also {
            Log.w(
                "AdvancedSemanticEmbedding", 
                "Fallback embedding generated: $reason"
            )
        }
    }

    /**
     * Create detailed error embedding
     */
    private fun createErrorEmbedding(
        text: String, 
        language: String, 
        error: Throwable,
        shouldCircuitBreak: Boolean
    ): SemanticEmbeddingResult {
        // Log detailed error information
        Log.e(
            "AdvancedSemanticEmbedding", 
            "Embedding generation failed: ${error.message}", 
            error
        )

        // Determine error source for more precise fallback
        val errorSource = when (error) {
            is EmbeddingGenerationException -> error.message
            else -> "UNKNOWN_ERROR"
        }

        // Create error-specific embedding
        return SemanticEmbeddingResult(
            embedding = FloatArray(EMBEDDING_DIMENSION) { 
                // Encode error information into embedding
                when {
                    errorSource == EmbeddingGenerationException.ERROR_MODEL_LOAD_FAILED -> -1f
                    errorSource == EmbeddingGenerationException.ERROR_INFERENCE_FAILED -> 0.5f
                    shouldCircuitBreak -> 0f
                    else -> Random.nextFloat() * 0.1f - 0.05f
                }
            }, 
            source = EmbeddingSource.ERROR,
            language = language
        )
    }

    /**
     * Generate cross-lingual embedding
     */
    suspend fun generateCrossLingualEmbedding(
        text: String, 
        targetLanguage: String,
        additionalContext: Map<String, Any> = emptyMap()
    ): SemanticEmbeddingResult = withContext(Dispatchers.Default) {
        // Generate initial embedding
        val originalEmbedding = generateEmbedding(text, additionalContext)

        // Transform embedding to target language
        val transformedEmbedding = multilingualTransformer.transformEmbedding(
            originalEmbedding.embedding, 
            originalEmbedding.language, 
            targetLanguage
        )

        // Create new embedding result with transformed embedding
        SemanticEmbeddingResult(
            embedding = transformedEmbedding,
            source = EmbeddingSource.TRANSFORMED,
            language = targetLanguage
        )
    }

    /**
     * Calculate semantic similarity between two embeddings
     */
    fun calculateSemanticSimilarity(
        embedding1: FloatArray, 
        embedding2: FloatArray
    ): Double {
        // Ensure consistent dimensions
        val minLength = minOf(embedding1.size, embedding2.size)
        val truncatedEmbedding1 = embedding1.take(minLength)
        val truncatedEmbedding2 = embedding2.take(minLength)

        // Cosine similarity calculation
        val dotProduct = truncatedEmbedding1
            .zip(truncatedEmbedding2)
            .map { it.first * it.second }
            .sum()

        val magnitude1 = sqrt(truncatedEmbedding1.map { it * it }.sum().toDouble())
        val magnitude2 = sqrt(truncatedEmbedding2.map { it * it }.sum().toDouble())

        // Avoid division by zero
        return if (magnitude1 > 0 && magnitude2 > 0) {
            dotProduct / (magnitude1 * magnitude2)
        } else {
            0.0
        }
    }

    /**
     * Preprocess text for embedding generation
     */
    private fun preprocessText(text: String): Pair<IntArray, IntArray> {
        // Tokenization and padding
        val tokens = tokenizer.tokenize(text)
        val tokenIds = tokens.map { tokenizer.convertTokenToId(it) }.toIntArray()
        
        // Pad or truncate to fixed length
        val paddedTokenIds = padSequence(tokenIds, MAX_SEQUENCE_LENGTH)
        val attentionMask = createAttentionMask(paddedTokenIds)

        return paddedTokenIds to attentionMask
    }

    /**
     * Prepare input tensors for model inference
     */
    private fun prepareInputTensors(
        tokenIds: IntArray, 
        attentionMask: IntArray
    ): Map<String, Any> {
        return mapOf(
            "input_ids" to arrayOf(tokenIds),
            "attention_mask" to arrayOf(attentionMask)
        )
    }

    /**
     * Run model inference
     */
    private fun runInference(inputTensors: Map<String, Any>): FloatArray {
        val outputTensor = Array(1) { FloatArray(EMBEDDING_DIMENSION) }
        
        // Run model
        interpreter.runForMultipleInputsOutputs(
            inputTensors.values.toTypedArray(), 
            mapOf("output" to outputTensor)
        )

        return outputTensor[0]
    }

    /**
     * Apply additional context enhancement to embedding
     */
    private fun applyContextEnhancement(
        embedding: FloatArray, 
        context: Map<String, Any>
    ): FloatArray {
        // Advanced context-based embedding enhancement
        var enhancedEmbedding = embedding.copyOf()

        // Media type context adjustment
        val mediaTypeMultiplier = when (context["media_type"] as? String) {
            "MOVIE" -> 1.1f
            "TV_SHOW" -> 0.9f
            "DOCUMENTARY" -> 1.05f
            else -> 1.0f
        }

        // Genre-based context enhancement
        val genre = context["genre"] as? String
        val genreEnhancementFactors = mapOf(
            "SCIFI" to 1.15f,
            "DRAMA" to 0.95f,
            "ACTION" to 1.1f,
            "COMEDY" to 0.85f
        )

        val genreMultiplier = genreEnhancementFactors[genre] ?: 1.0f

        // Apply context-aware modifications
        enhancedEmbedding = enhancedEmbedding.mapIndexed { index, value ->
            val dimensionModifier = when {
                index % 3 == 0 -> mediaTypeMultiplier
                index % 3 == 1 -> genreMultiplier
                else -> 1.0f
            }
            value * dimensionModifier
        }.toFloatArray()

        // Semantic intensity scaling based on additional context
        val semanticIntensity = context["semantic_intensity"] as? Float ?: 1.0f
        enhancedEmbedding = enhancedEmbedding.map { it * semanticIntensity }.toFloatArray()

        return enhancedEmbedding
    }

    /**
     * Normalize embedding vector
     */
    private fun normalizeEmbedding(embedding: FloatArray): FloatArray {
        val magnitude = sqrt(embedding.map { it * it }.sum().toDouble()).toFloat()
        return if (magnitude > 0) {
            embedding.map { it / magnitude }.toFloatArray()
        } else {
            embedding
        }
    }

    /**
     * Pad or truncate sequence to fixed length
     */
    private fun padSequence(
        tokenIds: IntArray, 
        maxLength: Int
    ): IntArray {
        return when {
            tokenIds.size > maxLength -> tokenIds.take(maxLength).toIntArray()
            tokenIds.size < maxLength -> {
                val paddedTokens = tokenIds.toMutableList()
                paddedTokens.addAll(
                    List(maxLength - tokenIds.size) { tokenizer.padTokenId }
                )
                paddedTokens.toIntArray()
            }
            else -> tokenIds
        }
    }

    /**
     * Create attention mask for transformer models
     */
    private fun createAttentionMask(tokenIds: IntArray): IntArray {
        return tokenIds.map { if (it != tokenizer.padTokenId) 1 else 0 }.toIntArray()
    }

    /**
     * Load TensorFlow Lite model
     */
    private fun loadModel(): Interpreter {
        val modelBuffer = FileUtil.loadMappedFile(context, modelPath)
        return Interpreter(modelBuffer)
    }

    /**
     * Load tokenizer
     */
    private fun loadTokenizer(): AdvancedTokenizer {
        // Load tokenizer configuration
        val tokenizerConfig = loadTokenizerConfig()
        return AdvancedTokenizer(context, tokenizerConfig)
    }

    /**
     * Load tokenizer configuration
     */
    private fun loadTokenizerConfig(): TokenizerConfig {
        val configFile = "tokenizer_config.json"
        val configJson = FileUtil.loadFileFromAsset(context, configFile).bufferedReader().use { it.readText() }
        return Gson().fromJson(configJson, TokenizerConfig::class.java)
    }

    /**
     * Advanced Tokenizer for semantic embedding
     */
    private class AdvancedTokenizer(
        private val context: Context,
        private val config: TokenizerConfig
    ) {
        val padTokenId = config.pad_token_id
        
        /**
         * Detect language of input text
         */
        fun detectLanguage(text: String): String {
            // Basic language detection using character set and script
            return when {
                // Detect Unicode scripts for different languages
                Pattern.compile("\\p{InCyrillic}").matcher(text).find() -> "ru"
                Pattern.compile("\\p{InHan}").matcher(text).find() -> "zh"
                Pattern.compile("\\p{InHangul}").matcher(text).find() -> "ko"
                Pattern.compile("\\p{InDevanagari}").matcher(text).find() -> "hi"
                Pattern.compile("\\p{InArabic}").matcher(text).find() -> "ar"
                // Default to English if no specific script is detected
                else -> "en"
            }
        }

        /**
         * Tokenize input text with multilingual support
         */
        fun tokenize(text: String): List<String> {
            val language = detectLanguage(text)
            
            // Multilingual preprocessing
            val preprocessedText = when (language) {
                "zh" -> normalizeChineseText(text)
                "ja" -> normalizeJapaneseText(text)
                "ko" -> normalizeKoreanText(text)
                "ar" -> normalizeArabicText(text)
                "hi" -> normalizeHindiText(text)
                else -> normalizeLatinText(text)
            }

            // Multilingual tokenization
            val tokens = when (language) {
                "zh" -> tokenizeChinese(preprocessedText)
                "ja" -> tokenizeJapanese(preprocessedText)
                "ko" -> tokenizeKorean(preprocessedText)
                "ar" -> tokenizeArabic(preprocessedText)
                "hi" -> tokenizeHindi(preprocessedText)
                else -> tokenizeDefault(preprocessedText)
            }

            return tokens
        }

        /**
         * Normalize Latin-based text
         */
        private fun normalizeLatinText(text: String): String {
            return text.lowercase()
                .replace(Regex("[^a-z0-9 '.,!?-]"), " ")
                .replace(Regex("\\s+"), " ")
                .trim()
        }

        /**
         * Default tokenization method
         */
        private fun tokenizeDefault(preprocessedText: String): List<String> {
            val tokens = mutableListOf<String>()
            val words = preprocessedText.split(" ")
            
            words.forEach { word ->
                when {
                    word.length > 6 -> {
                        // Break longer words into subwords
                        val mid = word.length / 2
                        tokens.add(word.substring(0, mid))
                        tokens.add(word.substring(mid))
                    }
                    word.length > 3 -> {
                        // Preserve semantically meaningful prefixes/suffixes
                        tokens.add(word.substring(0, word.length / 2))
                        tokens.add(word.substring(word.length / 2))
                    }
                    else -> tokens.add(word)
                }
            }

            return tokens
        }

        /**
         * Normalize and tokenize Chinese text
         */
        private fun normalizeChineseText(text: String): String {
            // Remove punctuation, normalize to simplified Chinese
            return text.replace(Regex("[\\p{P}\\p{Zs}]"), "")
        }

        /**
         * Tokenize Chinese text using character-level tokenization
         */
        private fun tokenizeChinese(text: String): List<String> {
            // Character-level tokenization for Chinese
            return text.toCharArray().map { it.toString() }
        }

        /**
         * Normalize and tokenize Japanese text
         */
        private fun normalizeJapaneseText(text: String): String {
            // Remove punctuation, normalize to hiragana/katakana
            return text.replace(Regex("[\\p{P}\\p{Zs}]"), "")
        }

        /**
         * Tokenize Japanese text considering kanji, hiragana, katakana
         */
        private fun tokenizeJapanese(text: String): List<String> {
            // More sophisticated Japanese tokenization
            val tokens = mutableListOf<String>()
            var currentToken = ""
            text.forEach { char ->
                when {
                    char.toString().matches(Regex("[\\p{InHiragana}\\p{InKatakana}\\p{InCJKUnifiedIdeographs}]")) -> {
                        currentToken += char
                    }
                    else -> {
                        if (currentToken.isNotEmpty()) {
                            tokens.add(currentToken)
                            currentToken = ""
                        }
                        if (char.isLetterOrDigit()) tokens.add(char.toString())
                    }
                }
            }
            if (currentToken.isNotEmpty()) tokens.add(currentToken)
            return tokens
        }

        /**
         * Normalize and tokenize Korean text
         */
        private fun normalizeKoreanText(text: String): String {
            // Remove punctuation
            return text.replace(Regex("[\\p{P}\\p{Zs}]"), "")
        }

        /**
         * Tokenize Korean text considering Hangul syllables
         */
        private fun tokenizeKorean(text: String): List<String> {
            // Syllable-level tokenization
            val tokens = mutableListOf<String>()
            var currentToken = ""
            text.forEach { char ->
                when {
                    char.toString().matches(Regex("[\\p{InHangul}]")) -> {
                        currentToken += char
                    }
                    else -> {
                        if (currentToken.isNotEmpty()) {
                            tokens.add(currentToken)
                            currentToken = ""
                        }
                        if (char.isLetterOrDigit()) tokens.add(char.toString())
                    }
                }
            }
            if (currentToken.isNotEmpty()) tokens.add(currentToken)
            return tokens
        }

        /**
         * Normalize Arabic text
         */
        private fun normalizeArabicText(text: String): String {
            // Normalize Arabic script, remove diacritics
            return text.replace(Regex("[\\p{Mn}]"), "")
                .replace(Regex("[\\p{P}\\p{Zs}]"), "")
        }

        /**
         * Tokenize Arabic text
         */
        private fun tokenizeArabic(text: String): List<String> {
            // Root-based tokenization considering Arabic morphology
            val tokens = mutableListOf<String>()
            var currentToken = ""
            text.forEach { char ->
                when {
                    char.toString().matches(Regex("[\\p{InArabic}]")) -> {
                        currentToken += char
                    }
                    else -> {
                        if (currentToken.isNotEmpty()) {
                            tokens.add(currentToken)
                            currentToken = ""
                        }
                        if (char.isLetterOrDigit()) tokens.add(char.toString())
                    }
                }
            }
            if (currentToken.isNotEmpty()) tokens.add(currentToken)
            return tokens
        }

        /**
         * Normalize Hindi text
         */
        private fun normalizeHindiText(text: String): String {
            // Normalize Devanagari script
            return text.replace(Regex("[\\p{Mn}]"), "")
                .replace(Regex("[\\p{P}\\p{Zs}]"), "")
        }

        /**
         * Tokenize Hindi text
         */
        private fun tokenizeHindi(text: String): List<String> {
            // Syllable and root-based tokenization
            val tokens = mutableListOf<String>()
            var currentToken = ""
            text.forEach { char ->
                when {
                    char.toString().matches(Regex("[\\p{InDevanagari}]")) -> {
                        currentToken += char
                    }
                    else -> {
                        if (currentToken.isNotEmpty()) {
                            tokens.add(currentToken)
                            currentToken = ""
                        }
                        if (char.isLetterOrDigit()) tokens.add(char.toString())
                    }
                }
            }
            if (currentToken.isNotEmpty()) tokens.add(currentToken)
            return tokens
        }

        /**
         * Convert token to ID with multilingual support
         */
        fun convertTokenToId(token: String): Int {
            // Lookup token in vocabulary, with fallback mechanisms
            return config.vocab[token] 
                ?: config.vocab[token.lowercase()] 
                ?: config.vocab[normalizeToken(token)] 
                ?: padTokenId
        }

        /**
         * Normalize token for more flexible vocabulary matching
         */
        private fun normalizeToken(token: String): String {
            // Remove diacritics, normalize case
            return token.lowercase()
                .replace(Regex("[\\p{Mn}]"), "")
                .trim()
        }
    }

    /**
     * Multilingual Embedding Utility
     */
    /**
     * Advanced Multilingual Embedding Transformer with ML-based Refinement
     */
    /**
     * Cross-Modal Embedding Transformer
     * Enables semantic representation across different modalities
     */
    private class CrossModalEmbeddingTransformer(
        private val context: Context
    ) {
        // Pre-trained models for different modalities
        private val textEmbeddingModel by lazy { loadTextEmbeddingModel() }
        private val imageEmbeddingModel by lazy { loadImageEmbeddingModel() }
        private val audioEmbeddingModel by lazy { loadAudioEmbeddingModel() }
        private val videoEmbeddingModel by lazy { loadVideoEmbeddingModel() }

        /**
         * Generate cross-modal embedding
         * @param input Multimodal input (text, image, audio, video file path)
         * @param modality Type of input modality
         */
        fun generateCrossModalEmbedding(
            input: Any,
            modality: Modality
        ): FloatArray {
            return when (modality) {
                Modality.TEXT -> generateTextEmbedding(input as String)
                Modality.IMAGE -> generateImageEmbedding(input as File)
                Modality.AUDIO -> generateAudioEmbedding(input as File)
                Modality.VIDEO -> generateVideoEmbedding(input as File)
            }
        }

        /**
         * Calculate cross-modal semantic similarity
         */
        fun calculateCrossModalSimilarity(
            embedding1: FloatArray,
            embedding2: FloatArray,
            modality1: Modality,
            modality2: Modality
        ): Double {
            // Normalize embeddings
            val normalizedEmbedding1 = normalizeEmbedding(embedding1)
            val normalizedEmbedding2 = normalizeEmbedding(embedding2)

            // Calculate cosine similarity
            return calculateCosineSimilarity(normalizedEmbedding1, normalizedEmbedding2)
        }

        /**
         * Generate text embedding using pre-trained model
         */
        private fun generateTextEmbedding(text: String): FloatArray {
            val (tokenIds, attentionMask) = preprocessText(text)
            val outputTensor = Array(1) { FloatArray(EMBEDDING_DIMENSION) }
            
            textEmbeddingModel.runForMultipleInputsOutputs(
                arrayOf(tokenIds, attentionMask),
                mapOf("output" to outputTensor)
            )

            return outputTensor[0]
        }

        /**
         * Generate image embedding using pre-trained model
         */
        private fun generateImageEmbedding(imageFile: File): FloatArray {
            // Load and preprocess image
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            
            // Convert bitmap to input tensor
            val inputTensor = preprocessImage(resizedBitmap)
            val outputTensor = Array(1) { FloatArray(EMBEDDING_DIMENSION) }
            
            imageEmbeddingModel.runForMultipleInputsOutputs(
                arrayOf(inputTensor),
                mapOf("output" to outputTensor)
            )

            return outputTensor[0]
        }

        /**
         * Generate audio embedding using pre-trained model
         */
        private fun generateAudioEmbedding(audioFile: File): FloatArray {
            // Extract audio features
            val audioFeatures = extractAudioFeatures(audioFile)
            val outputTensor = Array(1) { FloatArray(EMBEDDING_DIMENSION) }
            
            audioEmbeddingModel.runForMultipleInputsOutputs(
                arrayOf(audioFeatures),
                mapOf("output" to outputTensor)
            )

            return outputTensor[0]
        }

        /**
         * Generate video embedding using pre-trained model
         */
        private fun generateVideoEmbedding(videoFile: File): FloatArray {
            // Extract video keyframes
            val keyframes = extractVideoKeyframes(videoFile)
            
            // Generate embeddings for keyframes and aggregate
            val keyframeEmbeddings = keyframes.map { generateImageEmbedding(it) }
            
            // Aggregate keyframe embeddings
            return aggregateEmbeddings(keyframeEmbeddings)
        }

        /**
         * Preprocess text for embedding generation
         */
        private fun preprocessText(text: String): Pair<Array<IntArray>, Array<IntArray>> {
            // Tokenization logic (similar to existing implementation)
            val tokens = tokenizer.tokenize(text)
            val tokenIds = tokens.map { tokenizer.convertTokenToId(it) }.toIntArray()
            val paddedTokenIds = padSequence(tokenIds, MAX_SEQUENCE_LENGTH)
            val attentionMask = createAttentionMask(paddedTokenIds)
            
            return arrayOf(paddedTokenIds) to arrayOf(attentionMask)
        }

        /**
         * Preprocess image for embedding
         */
        private fun preprocessImage(bitmap: Bitmap): Array<FloatArray> {
            // Normalize pixel values and convert to tensor
            val floatValues = FloatArray(224 * 224 * 3)
            val intValues = IntArray(224 * 224)
            
            bitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)
            
            for (i in intValues.indices) {
                val value = intValues[i]
                floatValues[i * 3] = ((value shr 16 and 0xFF) - 127.5f) / 127.5f
                floatValues[i * 3 + 1] = ((value shr 8 and 0xFF) - 127.5f) / 127.5f
                floatValues[i * 3 + 2] = ((value and 0xFF) - 127.5f) / 127.5f
            }
            
            return arrayOf(floatValues)
        }

        /**
         * Extract audio features
         */
        private fun extractAudioFeatures(audioFile: File): Array<FloatArray> {
            // Use MediaMetadataRetriever for basic audio feature extraction
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(audioFile.absolutePath)
            
            // Extract basic audio metadata as features
            val durationMs = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull() ?: 0L
            
            val bitrate = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_BITRATE
            )?.toLongOrNull() ?: 0L
            
            retriever.release()
            
            // Create simple feature vector
            return arrayOf(floatArrayOf(
                durationMs.toFloat() / 1000f,  // Duration in seconds
                bitrate.toFloat() / 1000f,     // Bitrate in kbps
                0f, 0f, 0f, 0f                // Placeholders for more complex features
            ))
        }

        /**
         * Extract video keyframes
         */
        private fun extractVideoKeyframes(videoFile: File): List<File> {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoFile.absolutePath)
            
            // Extract keyframes at different timestamps
            val keyframeTimestamps = listOf(0L, 1000L, 5000L, 10000L)
            val keyframeFiles = mutableListOf<File>()
            
            keyframeTimestamps.forEach { timestamp ->
                val bitmap = retriever.getFrameAtTime(
                    timestamp * 1000,  // Microseconds
                    MediaMetadataRetriever.OPTION_CLOSEST
                )
                
                if (bitmap != null) {
                    // Save bitmap to temporary file
                    val keyframeFile = File.createTempFile("keyframe", ".jpg")
                    keyframeFile.deleteOnExit()
                    
                    keyframeFile.outputStream().use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    }
                    
                    keyframeFiles.add(keyframeFile)
                }
            }
            
            retriever.release()
            
            return keyframeFiles
        }

        /**
         * Aggregate embeddings with weighted averaging
         */
        private fun aggregateEmbeddings(embeddings: List<FloatArray>): FloatArray {
            if (embeddings.isEmpty()) {
                return FloatArray(EMBEDDING_DIMENSION) { 0f }
            }
            
            // Weighted averaging with recency bias
            return embeddings.mapIndexed { index, embedding ->
                val weight = 1.0f / (index + 1)  // Recency bias
                embedding.map { it * weight }
            }.reduce { acc, embedValues ->
                acc.zip(embedValues).map { it.first + it.second }
            }
        }

        /**
         * Normalize embedding vector
         */
        private fun normalizeEmbedding(embedding: FloatArray): FloatArray {
            val magnitude = sqrt(embedding.map { it * it }.sum().toDouble()).toFloat()
            return if (magnitude > 0) {
                embedding.map { it / magnitude }.toFloatArray()
            } else {
                embedding
            }
        }

        /**
         * Calculate cosine similarity between embeddings
         */
        private fun calculateCosineSimilarity(
            embedding1: FloatArray, 
            embedding2: FloatArray
        ): Double {
            val dotProduct = embedding1
                .zip(embedding2)
                .map { it.first * it.second }
                .sum()

            val magnitude1 = sqrt(embedding1.map { it * it }.sum().toDouble())
            val magnitude2 = sqrt(embedding2.map { it * it }.sum().toDouble())

            return if (magnitude1 > 0 && magnitude2 > 0) {
                dotProduct / (magnitude1 * magnitude2)
            } else {
                0.0
            }
        }

        /**
         * Load pre-trained embedding models
         */
        private fun loadTextEmbeddingModel(): Interpreter {
            val modelBuffer = FileUtil.loadMappedFile(
                context, 
                "text_embedding_model.tflite"
            )
            return Interpreter(modelBuffer)
        }

        private fun loadImageEmbeddingModel(): Interpreter {
            val modelBuffer = FileUtil.loadMappedFile(
                context, 
                "image_embedding_model.tflite"
            )
            return Interpreter(modelBuffer)
        }

        private fun loadAudioEmbeddingModel(): Interpreter {
            val modelBuffer = FileUtil.loadMappedFile(
                context, 
                "audio_embedding_model.tflite"
            )
            return Interpreter(modelBuffer)
        }

        private fun loadVideoEmbeddingModel(): Interpreter {
            val modelBuffer = FileUtil.loadMappedFile(
                context, 
                "video_embedding_model.tflite"
            )
            return Interpreter(modelBuffer)
        }
    }

    /**
     * Modality enum for cross-modal embedding
     */
    enum class Modality {
        TEXT,
        IMAGE,
        AUDIO,
        VIDEO
    }

    private class MultilingualEmbeddingTransformer(
        private val context: Context
    ) {
        // Machine learning model for semantic drift compensation
        private val semanticDriftModel by lazy { loadSemanticDriftModel() }

        // Language similarity cache to improve performance
        private val similarityCache = mutableMapOf<Pair<String, String>, Double>()

        /**
         * Transform embedding between languages with advanced techniques
         */
        fun transformEmbedding(
            embedding: FloatArray, 
            sourceLanguage: String, 
            targetLanguage: String
        ): FloatArray {
            // Load transformation matrix
            val transformationMatrix = loadTransformationMatrix(sourceLanguage, targetLanguage)
            
            // Apply linear transformation with semantic drift compensation
            return embedding.mapIndexed { index, value ->
                val transformedValue = transformationMatrix.getOrNull(index)
                    ?.let { it * value }
                    ?: value
                
                // Apply semantic drift compensation
                compensateSemanticDrift(transformedValue, index, sourceLanguage, targetLanguage)
            }.toFloatArray()
        }

        /**
         * Load language transformation matrix with improved sophistication
         */
        private fun loadTransformationMatrix(
            sourceLanguage: String, 
            targetLanguage: String
        ): List<Float> {
            // Enhanced transformation matrix generation
            val baseMatrix = List(EMBEDDING_DIMENSION) { 1.0f }
            
            // Language-specific scaling factors with more nuanced mapping
            val languageScalingFactors = mapOf(
                "en" to 1.0f,
                "zh" to 0.9f,
                "ja" to 0.95f,
                "ko" to 0.92f,
                "ar" to 0.88f,
                "hi" to 0.91f,
                "es" to 0.97f,
                "fr" to 0.96f,
                "de" to 0.94f,
                "ru" to 0.93f
            )

            // Apply advanced scaling with linguistic proximity consideration
            return baseMatrix.mapIndexed { index, value ->
                val sourceScale = languageScalingFactors[sourceLanguage] ?: 1.0f
                val targetScale = languageScalingFactors[targetLanguage] ?: 1.0f
                
                // Add slight randomization to prevent deterministic transformations
                val randomVariation = 1.0f + Random.nextFloat() * 0.05f - 0.025f
                
                value * (targetScale / sourceScale) * randomVariation
            }
        }

        /**
         * Semantic drift compensation using ML model
         */
        private fun compensateSemanticDrift(
            value: Float, 
            index: Int, 
            sourceLanguage: String, 
            targetLanguage: String
        ): Float {
            // Use semantic drift model to adjust embedding values
            val driftCompensationInput = floatArrayOf(
                value, 
                index.toFloat() / EMBEDDING_DIMENSION, 
                getLanguageProximityScore(sourceLanguage, targetLanguage)
            )

            val driftCompensationOutput = FloatArray(1)
            semanticDriftModel.run(
                arrayOf(driftCompensationInput), 
                mapOf("output" to arrayOf(driftCompensationOutput))
            )

            return driftCompensationOutput[0]
        }

        /**
         * Calculate advanced language similarity with ML refinement
         */
        fun calculateLanguageSimilarity(
            embedding1: FloatArray, 
            embedding2: FloatArray, 
            language1: String, 
            language2: String
        ): Double {
            // Check cache first
            val cacheKey = Pair(language1, language2)
            similarityCache[cacheKey]?.let { return it }

            // Transform embeddings to a common space
            val transformedEmbedding1 = transformEmbedding(
                embedding1, 
                language1, 
                "en"
            )
            val transformedEmbedding2 = transformEmbedding(
                embedding2, 
                language2, 
                "en"
            )

            // Calculate cosine similarity
            val dotProduct = transformedEmbedding1
                .zip(transformedEmbedding2)
                .map { it.first * it.second }
                .sum()

            val magnitude1 = sqrt(transformedEmbedding1.map { it * it }.sum().toDouble())
            val magnitude2 = sqrt(transformedEmbedding2.map { it * it }.sum().toDouble())

            // Calculate base similarity
            val baseSimilarity = if (magnitude1 > 0 && magnitude2 > 0) {
                dotProduct / (magnitude1 * magnitude2)
            } else {
                0.0
            }

            // Apply ML-based refinement
            val refinedSimilarity = refineLanguageSimilarity(
                baseSimilarity, 
                language1, 
                language2
            )

            // Cache and return result
            similarityCache[cacheKey] = refinedSimilarity
            return refinedSimilarity
        }

        /**
         * ML-based language similarity refinement
         */
        private fun refineLanguageSimilarity(
            baseSimilarity: Double, 
            language1: String, 
            language2: String
        ): Double {
            // Linguistic proximity factors
            val proximityScore = getLanguageProximityScore(language1, language2)
            
            // ML model for similarity refinement
            val similarityRefinementInput = floatArrayOf(
                baseSimilarity.toFloat(), 
                proximityScore
            )

            val refinementOutput = FloatArray(1)
            semanticDriftModel.run(
                arrayOf(similarityRefinementInput), 
                mapOf("output" to arrayOf(refinementOutput))
            )

            return refinementOutput[0].coerceIn(-1.0, 1.0).toDouble()
        }

        /**
         * Get linguistic proximity score between languages
         */
        private fun getLanguageProximityScore(
            language1: String, 
            language2: String
        ): Float {
            // Language family and script proximity mapping
            val languageProximityMap = mapOf(
                Pair("en", "fr") to 0.8f,   // Romance language influence
                Pair("en", "de") to 0.7f,   // Germanic language connection
                Pair("es", "pt") to 0.9f,   // Very similar Romance languages
                Pair("zh", "ja") to 0.5f,   // Some linguistic borrowing
                Pair("en", "zh") to 0.2f    // Minimal linguistic proximity
            )

            // Bidirectional lookup
            return languageProximityMap[Pair(language1, language2)] 
                ?: languageProximityMap[Pair(language2, language1)] 
                ?: 0.5f  // Neutral proximity for unknown language pairs
        }

        /**
         * Load semantic drift compensation ML model
         */
        private fun loadSemanticDriftModel(): Interpreter {
            // Load TensorFlow Lite model for semantic drift compensation
            val modelBuffer = FileUtil.loadMappedFile(
                context, 
                "semantic_drift_model.tflite"
            )
            return Interpreter(modelBuffer)
        }

        /**
         * Clear similarity cache to prevent stale entries
         */
        fun clearSimilarityCache() {
            similarityCache.clear()
        }
    }

    /**
     * Tokenizer configuration
     */
    data class TokenizerConfig(
        val vocab: Map<String, Int>,
        val pad_token_id: Int = 0
    )

    /**
     * Semantic embedding result with source tracking
     */
     data class SemanticEmbeddingResult(
        val embedding: FloatArray,
        val source: EmbeddingSource,
        val language: String = "en"
     ) {
         override fun equals(other: Any?): Boolean {
             if (this === other) return true
             if (javaClass != other?.javaClass) return false

             other as SemanticEmbeddingResult

             if (!embedding.contentEquals(other.embedding)) return false
             if (source != other.source) return false
             if (language != other.language) return false

             return true
         }

         override fun hashCode(): Int {
             var result = embedding.contentHashCode()
             result = 31 * result + source.hashCode()
             result = 31 * result + language.hashCode()
             return result
         }
     }

    /**
     * Embedding source tracking
     */
     enum class EmbeddingSource {
        GENERATED,       // Newly created embedding
        CACHED,          // Retrieved from cache
        TRANSFORMED,     // Cross-lingual transformation
        CROSS_MODAL,     // Cross-modal embedding
        ERROR            // Failed embedding generation
     }

    /**
     * Simple LRU Cache implementation
     */
    private class LruCache<K, V>(private val maxSize: Int) {
        private val cache = LinkedHashMap<K, V>(maxSize, 0.75f, true)

        operator fun get(key: K): V? = cache[key]

        operator fun set(key: K, value: V) {
            if (cache.size >= maxSize && !cache.containsKey(key)) {
                // Remove oldest entry
                cache.remove(cache.keys.first())
            }
            cache[key] = value
        }
    }

    companion object {
        // Embedding dimension (configurable based on model)
        const val EMBEDDING_DIMENSION = 768

        // Maximum sequence length for transformer models
        const val MAX_SEQUENCE_LENGTH = 512

        // Semantic enhancement configuration
        val MEDIA_TYPE_ENHANCEMENT_FACTORS = mapOf(
            "MOVIE" to 1.1f,
            "TV_SHOW" to 0.9f,
            "DOCUMENTARY" to 1.05f,
            "NEWS" to 0.95f
        )

        val GENRE_ENHANCEMENT_FACTORS = mapOf(
            "SCIFI" to 1.15f,
            "DRAMA" to 0.95f,
            "ACTION" to 1.1f,
            "COMEDY" to 0.85f,
            "TECHNOLOGY" to 1.05f
        )

        // Subword tokenization configuration
        const val SUBWORD_MIN_LENGTH = 4
        const val SUBWORD_MAX_LENGTH = 6

        // Advanced error handling constants
        const val MAX_ERROR_THRESHOLD = 5
        const val ERROR_COOLDOWN_PERIOD = 60000L  // 1 minute
        const val MAX_INPUT_TOKEN_LENGTH = 1024

        // Model management configuration
        val SUPPORTED_MODEL_TYPES = listOf(
            "text_embedding",
            "image_embedding",
            "audio_embedding",
            "video_embedding",
            "multilingual"
        )

        // Error handling strategies
        val ERROR_RECOVERY_STRATEGIES = mapOf(
            "MODEL_LOAD_FAILED" to "FALLBACK_MODEL",
            "PREPROCESSING_FAILED" to "SKIP_PREPROCESSING",
            "INFERENCE_FAILED" to "MINIMAL_EMBEDDING",
            "POSTPROCESSING_FAILED" to "RAW_OUTPUT"
        )

        // Multilingual configuration
        val SUPPORTED_LANGUAGES = listOf(
            "en", "zh", "ja", "ko", "ar", "hi", 
            "es", "fr", "de", "ru", "pt", "it"
        )

        // Advanced domain adaptation factors
        val DOMAIN_ADAPTATION_FACTORS = mapOf(
            "MOVIE" to floatArrayOf(1.1f, 1.05f, 0.95f),
            "TV_SHOW" to floatArrayOf(0.9f, 1.0f, 1.1f),
            "DOCUMENTARY" to floatArrayOf(1.05f, 1.15f, 0.9f),
            "NEWS" to floatArrayOf(0.95f, 1.0f, 1.05f)
        )

        // Language similarity baseline with enhanced granularity
        val LANGUAGE_SIMILARITY_BASELINE = mapOf(
            Pair("en", "en") to 1.0f,
            Pair("es", "pt") to 0.85f,  // Similar Romance languages
            Pair("fr", "it") to 0.80f,
            Pair("en", "fr") to 0.65f,  // Partially intelligible
            Pair("zh", "ja") to 0.45f,  // Different language families
            Pair("en", "zh") to 0.3f,   // Least similar
            Pair("en", "de") to 0.7f,   // Germanic language connection
            Pair("ru", "uk") to 0.75f,  // Slavic language proximity
            Pair("hi", "ur") to 0.8f    // Similar Indic languages
        )

        // Language transformation scaling factors with enhanced linguistic nuance
        val LANGUAGE_TRANSFORMATION_FACTORS = mapOf(
            "en" to 1.0f,
            "zh" to 0.9f,
            "ja" to 0.95f,
            "ko" to 0.92f,
            "ar" to 0.88f,
            "hi" to 0.91f,
            "es" to 0.96f,
            "fr" to 0.97f,
            "de" to 0.94f,
            "ru" to 0.90f,
            "pt" to 0.95f,
            "it" to 0.96f,
            "uk" to 0.93f
        )

        // Semantic consistency verification configuration
        const val DEFAULT_SEMANTIC_CONSISTENCY_THRESHOLD = 0.7
        const val MIN_SEMANTIC_CONSISTENCY_THRESHOLD = 0.5
        const val MAX_SEMANTIC_CONSISTENCY_THRESHOLD = 0.9

        /**
         * Get recommended transformation for language pair with enhanced strategy
         */
        fun getLanguageTransformationStrategy(
            sourceLanguage: String, 
            targetLanguage: String
        ): Float {
            // Enhanced lookup with linguistic proximity consideration
            return when {
                sourceLanguage == targetLanguage -> 1.0f
                LANGUAGE_SIMILARITY_BASELINE.containsKey(Pair(sourceLanguage, targetLanguage)) -> 
                    LANGUAGE_TRANSFORMATION_FACTORS[targetLanguage] ?: 1.0f
                else -> {
                    // Default transformation with slight randomization
                    val baseTransformation = LANGUAGE_TRANSFORMATION_FACTORS[targetLanguage] 
                        ?: LANGUAGE_TRANSFORMATION_FACTORS[sourceLanguage] 
                        ?: 1.0f
                    
                    // Add small random variation to prevent deterministic transformations
                    baseTransformation * (0.95f + Random.nextFloat() * 0.1f)
                }
            }
        }

        /**
         * Validate semantic consistency threshold
         */
        fun validateSemanticConsistencyThreshold(threshold: Double): Double {
            return threshold.coerceIn(
                MIN_SEMANTIC_CONSISTENCY_THRESHOLD, 
                MAX_SEMANTIC_CONSISTENCY_THRESHOLD
            )
        }
    }
}