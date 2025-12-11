package com.shareconnect.plexconnect.nlp

import android.content.Context
import android.util.Log
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier
import org.tensorflow.lite.task.text.nlclassifier.NlClassifier
import java.io.IOException
import java.nio.MappedByteBuffer

/**
 * Advanced NLP Metadata Analysis for Media Recommendations
 */
class MediaMetadataAnalyzer(private val context: Context) {

    // Lazy-initialized NLP models
    private val genreClassifier: NlClassifier? by lazy { loadGenreClassifier() }
    private val sentimentAnalyzer: NlClassifier? by lazy { loadSentimentAnalyzer() }
    private val embeddings by lazy { loadWordEmbeddings() }

    /**
     * Analyze media item metadata for enhanced recommendation insights
     */
    suspend fun analyzeMetadata(mediaItem: PlexMediaItem): MetadataAnalysisResult = withContext(Dispatchers.Default) {
        try {
            MetadataAnalysisResult(
                title = analyzeTitle(mediaItem.title ?: ""),
                summary = analyzeSummary(mediaItem.summary ?: ""),
                genres = predictGenres(mediaItem.summary ?: ""),
                sentimentScore = analyzeSentiment(mediaItem.summary ?: ""),
                semanticEmbedding = generateSemanticEmbedding(mediaItem)
            )
        } catch (e: Exception) {
            Log.e("MediaMetadataAnalyzer", "Metadata analysis failed", e)
            MetadataAnalysisResult() // Return empty result on failure
        }
    }

    /**
     * Advanced title analysis
     */
    private fun analyzeTitle(title: String): TitleAnalysis {
        return TitleAnalysis(
            normalizedTitle = title.lowercase(),
            wordCount = title.split("\\s+".toRegex()).size,
            containsSpecialChars = title.contains("[^a-zA-Z0-9 ]".toRegex())
        )
    }

    /**
     * Comprehensive summary analysis
     */
    private fun analyzeSummary(summary: String): SummaryAnalysis {
        return SummaryAnalysis(
            wordCount = summary.split("\\s+".toRegex()).size,
            sentenceCount = summary.split("[.!?]".toRegex()).size,
            averageWordLength = summary.split("\\s+".toRegex())
                .map { it.length }
                .average()
                .takeIf { it.isFinite() } ?: 0.0
        )
    }

    /**
     * Genre prediction using ML classifier
     */
    private fun predictGenres(summary: String): List<String> {
        return try {
            val categories = genreClassifier?.classify(summary)
            categories?.filter { it.score > GENRE_CONFIDENCE_THRESHOLD }
                ?.map { it.label }
                ?.take(3) // Top 3 genres
                ?: emptyList()
        } catch (e: Exception) {
            Log.w("MediaMetadataAnalyzer", "Genre prediction failed", e)
            emptyList()
        }
    }

    /**
     * Sentiment analysis of summary
     */
    private fun analyzeSentiment(summary: String): Double {
        return try {
            val categories = sentimentAnalyzer?.classify(summary)
            categories?.firstOrNull()?.score 
                ?: 0.5 // Neutral sentiment
        } catch (e: Exception) {
            Log.w("MediaMetadataAnalyzer", "Sentiment analysis failed", e)
            0.5
        }
    }

    /**
     * Generate semantic embedding for media item
     */
    private fun generateSemanticEmbedding(mediaItem: PlexMediaItem): FloatArray {
        val combinedText = "${mediaItem.title} ${mediaItem.summary}"
        return embeddings.encode(combinedText)
    }

    /**
     * Load genre classification model
     */
    private fun loadGenreClassifier(): NlClassifier? {
        return try {
            BertNLClassifier.createFromFile(context, "genre_classifier.tflite")
        } catch (e: IOException) {
            Log.w("MediaMetadataAnalyzer", "Failed to load genre classifier, using null", e)
            null
        }
    }

    /**
     * Load sentiment analysis model
     */
    private fun loadSentimentAnalyzer(): NlClassifier? {
        return try {
            BertNLClassifier.createFromFile(context, "sentiment_analyzer.tflite")
        } catch (e: IOException) {
            Log.w("MediaMetadataAnalyzer", "Failed to load sentiment analyzer, using null", e)
            null
        }
    }

    /**
     * Load word embeddings model
     */
    private fun loadWordEmbeddings(): WordEmbeddings {
        return try {
            WordEmbeddings(context, "word_embeddings.tflite")
        } catch (e: IOException) {
            Log.e("MediaMetadataAnalyzer", "Failed to load word embeddings", e)
            throw e
        }
    }

    /**
     * Word Embeddings Utility Class
     */
    private class WordEmbeddings(context: Context, modelPath: String) {
        private val interpreter: Interpreter
        private val inputTensor: MappedByteBuffer

        init {
            inputTensor = FileUtil.loadMappedFile(context, modelPath)
            interpreter = Interpreter(inputTensor)
        }

        fun encode(text: String): FloatArray {
            // Placeholder for actual embedding generation
            // In a real implementation, this would use tokenization and model inference
            val input = preprocessText(text)
            val output = Array(1) { FloatArray(300) } // 300-dimensional embedding
            interpreter.run(input, output)
            return output[0]
        }

        private fun preprocessText(text: String): Array<FloatArray> {
            // Tokenization and preprocessing
            // This is a simplified placeholder
            return arrayOf(text.map { it.code.toFloat() }.toFloatArray())
        }
    }

    /**
     * Metadata Analysis Result
     */
    data class MetadataAnalysisResult(
        val title: TitleAnalysis = TitleAnalysis(),
        val summary: SummaryAnalysis = SummaryAnalysis(),
        val genres: List<String> = emptyList(),
        val sentimentScore: Double = 0.5,
        val semanticEmbedding: FloatArray = floatArrayOf()
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MetadataAnalysisResult

            if (title != other.title) return false
            if (summary != other.summary) return false
            if (genres != other.genres) return false
            if (sentimentScore != other.sentimentScore) return false
            if (!semanticEmbedding.contentEquals(other.semanticEmbedding)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = title.hashCode()
            result = 31 * result + summary.hashCode()
            result = 31 * result + genres.hashCode()
            result = 31 * result + sentimentScore.hashCode()
            result = 31 * result + semanticEmbedding.contentHashCode()
            return result
        }
    }

    /**
     * Title Analysis Details
     */
    data class TitleAnalysis(
        val normalizedTitle: String = "",
        val wordCount: Int = 0,
        val containsSpecialChars: Boolean = false
    )

    /**
     * Summary Analysis Details
     */
    data class SummaryAnalysis(
        val wordCount: Int = 0,
        val sentenceCount: Int = 0,
        val averageWordLength: Double = 0.0
    )

    companion object {
        // Confidence threshold for genre prediction
        private const val GENRE_CONFIDENCE_THRESHOLD = 0.7f
    }
}