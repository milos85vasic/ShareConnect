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


package com.shareconnect.qa.ai.executor

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * AI client for interacting with Claude API
 */
class AIClient(private val apiKey: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    private val mediaType = "application/json".toMediaType()

    /**
     * Send a prompt to Claude API
     */
    fun sendPrompt(
        prompt: String,
        systemPrompt: String = "",
        maxTokens: Int = 4096,
        temperature: Double = 0.7
    ): AIResponse {
        val requestBody = ClaudeRequest(
            model = "claude-3-5-sonnet-20241022",
            maxTokens = maxTokens,
            temperature = temperature,
            system = systemPrompt.ifEmpty { null },
            messages = listOf(
                Message(role = "user", content = prompt)
            )
        )

        val requestBodyJson = json.encodeToString(requestBody)

        val request = Request.Builder()
            .url("https://api.anthropic.com/v1/messages")
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("content-type", "application/json")
            .post(requestBodyJson.toRequestBody(mediaType))
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return AIResponse(
                        success = false,
                        text = "",
                        error = "API request failed: ${response.code} ${response.message}"
                    )
                }

                val responseBody = response.body?.string() ?: ""
                val claudeResponse = json.decodeFromString<ClaudeResponse>(responseBody)

                AIResponse(
                    success = true,
                    text = claudeResponse.content.firstOrNull()?.text ?: "",
                    stopReason = claudeResponse.stop_reason
                )
            }
        } catch (e: Exception) {
            AIResponse(
                success = false,
                text = "",
                error = "API request exception: ${e.message}"
            )
        }
    }

    /**
     * Analyze a screenshot for UI validation
     */
    fun analyzeScreenshot(
        screenshotBase64: String,
        question: String,
        context: String = ""
    ): AIResponse {
        val fullPrompt = buildString {
            if (context.isNotEmpty()) {
                appendLine("Context: $context")
                appendLine()
            }
            appendLine("Question: $question")
            appendLine()
            appendLine("Please analyze the screenshot and answer the question.")
        }

        val requestBody = ClaudeVisionRequest(
            model = "claude-3-5-sonnet-20241022",
            maxTokens = 2048,
            messages = listOf(
                VisionMessage(
                    role = "user",
                    content = listOf(
                        ContentBlock(
                            type = "image",
                            source = ImageSource(
                                type = "base64",
                                media_type = "image/png",
                                data = screenshotBase64
                            )
                        ),
                        ContentBlock(
                            type = "text",
                            text = fullPrompt
                        )
                    )
                )
            )
        )

        val requestBodyJson = json.encodeToString(requestBody)

        val request = Request.Builder()
            .url("https://api.anthropic.com/v1/messages")
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("content-type", "application/json")
            .post(requestBodyJson.toRequestBody(mediaType))
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return AIResponse(
                        success = false,
                        text = "",
                        error = "Screenshot analysis failed: ${response.code}"
                    )
                }

                val responseBody = response.body?.string() ?: ""
                val claudeResponse = json.decodeFromString<ClaudeResponse>(responseBody)

                AIResponse(
                    success = true,
                    text = claudeResponse.content.firstOrNull()?.text ?: "",
                    stopReason = claudeResponse.stop_reason
                )
            }
        } catch (e: Exception) {
            AIResponse(
                success = false,
                text = "",
                error = "Screenshot analysis exception: ${e.message}"
            )
        }
    }
}

/**
 * Claude API request models
 */
@Serializable
private data class ClaudeRequest(
    val model: String,
    val maxTokens: Int,
    val temperature: Double,
    val system: String? = null,
    val messages: List<Message>
)

@Serializable
private data class Message(
    val role: String,
    val content: String
)

@Serializable
private data class ClaudeVisionRequest(
    val model: String,
    val maxTokens: Int,
    val messages: List<VisionMessage>
)

@Serializable
private data class VisionMessage(
    val role: String,
    val content: List<ContentBlock>
)

@Serializable
private data class ContentBlock(
    val type: String,
    val text: String? = null,
    val source: ImageSource? = null
)

@Serializable
private data class ImageSource(
    val type: String,
    val media_type: String,
    val data: String
)

@Serializable
private data class ClaudeResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<ContentItem>,
    val stop_reason: String?,
    val usage: Usage
)

@Serializable
private data class ContentItem(
    val type: String,
    val text: String
)

@Serializable
private data class Usage(
    val input_tokens: Int,
    val output_tokens: Int
)

/**
 * AI response wrapper
 */
data class AIResponse(
    val success: Boolean,
    val text: String,
    val error: String? = null,
    val stopReason: String? = null
)
