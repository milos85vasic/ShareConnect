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


package com.shareconnect.homeassistantconnect.data.api

import com.shareconnect.homeassistantconnect.data.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Home Assistant API client for home automation and IoT device control
 *
 * @param serverUrl Base URL of Home Assistant server (e.g., "http://192.168.1.100:8123")
 * @param accessToken Long-lived access token for authentication
 * @param homeAssistantApiService Optional service instance for dependency injection (testing)
 */
class HomeAssistantApiClient(
    private val serverUrl: String,
    private val accessToken: String,
    homeAssistantApiService: HomeAssistantApiService? = null
) {

    private val service: HomeAssistantApiService by lazy {
        homeAssistantApiService ?: createService()
    }

    private fun createService(): HomeAssistantApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(HomeAssistantApiService::class.java)
    }

    private fun getAuthHeader(): String = "Bearer $accessToken"

    suspend fun getApiStatus(): Result<HomeAssistantApiStatus> {
        return try {
            val response = service.getApiStatus(getAuthHeader())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get API status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getConfig(): Result<HomeAssistantConfig> {
        return try {
            val response = service.getConfig(getAuthHeader())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get config: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEvents(): Result<List<HomeAssistantEvent>> {
        return try {
            val response = service.getEvents(getAuthHeader())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get events: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getServices(): Result<List<HomeAssistantServiceDomain>> {
        return try {
            val response = service.getServices(getAuthHeader())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get services: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStates(): Result<List<HomeAssistantState>> {
        return try {
            val response = service.getStates(getAuthHeader())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get states: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getState(entityId: String): Result<HomeAssistantState> {
        return try {
            val response = service.getState(entityId, getAuthHeader())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get state: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setState(entityId: String, state: HomeAssistantState): Result<HomeAssistantState> {
        return try {
            val response = service.setState(entityId, getAuthHeader(), state)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to set state: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun callService(domain: String, service: String, request: ServiceCallRequest): Result<List<ServiceCallResponse>> {
        return try {
            val response = this.service.callService(domain, service, getAuthHeader(), request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to call service: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fireEvent(eventType: String, data: Map<String, Any>): Result<ServiceCallResponse> {
        return try {
            val response = service.fireEvent(eventType, getAuthHeader(), data)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fire event: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getErrorLog(): Result<String> {
        return try {
            val response = service.getErrorLog(getAuthHeader())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get error log: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCameraImage(entityId: String): Result<ByteArray> {
        return try {
            val response = service.getCameraImage(entityId, getAuthHeader())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get camera image: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistory(
        timestamp: String,
        filterEntityId: String? = null,
        endTime: String? = null,
        minimalResponse: Boolean? = null,
        noAttributes: Boolean? = null,
        significantChangesOnly: Boolean? = null
    ): Result<List<List<HomeAssistantState>>> {
        return try {
            val response = service.getHistory(
                timestamp, getAuthHeader(), filterEntityId, endTime,
                minimalResponse, noAttributes, significantChangesOnly
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get history: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLogbook(
        timestamp: String,
        entity: String? = null,
        endTime: String? = null
    ): Result<List<HomeAssistantLogbookEntry>> {
        return try {
            val response = service.getLogbook(timestamp, getAuthHeader(), entity, endTime)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get logbook: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun renderTemplate(template: String): Result<String> {
        return try {
            val response = service.renderTemplate(getAuthHeader(), TemplateRequest(template))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to render template: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun handleIntent(request: IntentRequest): Result<IntentResponse> {
        return try {
            val response = service.handleIntent(getAuthHeader(), request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to handle intent: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCalendarEvents(
        entityId: String,
        start: String? = null,
        end: String? = null
    ): Result<List<CalendarEvent>> {
        return try {
            val response = service.getCalendarEvents(entityId, getAuthHeader(), start, end)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get calendar events: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
