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


package com.shareconnect.netdataconnect.data.api

import com.shareconnect.netdataconnect.data.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Netdata API client for system monitoring and metrics collection
 *
 * Provides access to:
 * - Server information and version
 * - Charts and metrics discovery
 * - Real-time and historical data
 * - Alarms and alerts
 * - All metrics snapshots
 * - Contexts and chart groupings
 *
 * @param serverUrl Base URL of Netdata server (e.g., "http://192.168.1.100:19999")
 * @param netdataApiService Optional service instance for dependency injection (testing)
 */
class NetdataApiClient(
    private val serverUrl: String,
    netdataApiService: NetdataApiService? = null
) {

    private val service: NetdataApiService by lazy {
        netdataApiService ?: createService()
    }

    private fun createService(): NetdataApiService {
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

        return retrofit.create(NetdataApiService::class.java)
    }

    /**
     * Get server information and version
     *
     * @return Result containing NetdataInfo or error
     */
    suspend fun getInfo(): Result<NetdataInfo> {
        return try {
            val response = service.getInfo()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get server info: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all available charts
     *
     * @return Result containing NetdataCharts or error
     */
    suspend fun getCharts(): Result<NetdataCharts> {
        return try {
            val response = service.getCharts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get charts: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get data for a specific chart
     *
     * @param chart Chart ID (e.g., "system.cpu", "system.ram")
     * @param after Start time (unix timestamp or negative for relative seconds)
     * @param before End time (unix timestamp)
     * @param points Number of data points to return
     * @param group Grouping method (average, max, min, sum)
     * @param gtime Group time in seconds
     * @param options Additional options (null2zero, percentage, jsonwrap)
     * @param dimensions Comma-separated dimension names
     * @param format Response format (json, csv, datasource)
     * @return Result containing NetdataChartData or error
     */
    suspend fun getData(
        chart: String,
        after: Long? = null,
        before: Long? = null,
        points: Int? = null,
        group: String? = "average",
        gtime: Int? = null,
        options: String? = null,
        dimensions: String? = null,
        format: String? = "json"
    ): Result<NetdataChartData> {
        return try {
            val response = service.getData(
                chart = chart,
                after = after,
                before = before,
                points = points,
                group = group,
                gtime = gtime,
                options = options,
                dimensions = dimensions,
                format = format
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get chart data: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all active alarms
     *
     * @param all Include all alarms (not just active)
     * @return Result containing NetdataAlarms or error
     */
    suspend fun getAlarms(all: Boolean? = null): Result<NetdataAlarms> {
        return try {
            val response = service.getAlarms(all)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get alarms: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get alarm log with optional filtering
     *
     * @param after Start time filter
     * @param alarm Specific alarm name filter
     * @return Result containing list of alarms or error
     */
    suspend fun getAlarmLog(
        after: Long? = null,
        alarm: String? = null
    ): Result<List<NetdataAlarm>> {
        return try {
            val response = service.getAlarmLog(after, alarm)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get alarm log: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all metrics in a single snapshot
     *
     * @param format Response format (json, prometheus, shell)
     * @param help Include help text
     * @param types Include metric types
     * @param timestamps Include timestamps
     * @param names Include metric names
     * @param oldunits Use old units format
     * @param hideunits Hide units
     * @param server Include server info
     * @return Result containing NetdataAllMetrics or error
     */
    suspend fun getAllMetrics(
        format: String = "json",
        help: String? = null,
        types: String? = null,
        timestamps: String? = null,
        names: String? = null,
        oldunits: String? = null,
        hideunits: String? = null,
        server: String? = null
    ): Result<NetdataAllMetrics> {
        return try {
            val response = service.getAllMetrics(
                format = format,
                help = help,
                types = types,
                timestamps = timestamps,
                names = names,
                oldunits = oldunits,
                hideunits = hideunits,
                server = server
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get all metrics: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get badge value (simple metric value as SVG)
     *
     * @param chart Chart ID
     * @param alarm Alarm name (alternative to chart)
     * @param dimension Specific dimension
     * @param after Time range start
     * @param before Time range end
     * @param group Grouping method
     * @param options Options
     * @param label Custom label
     * @param units Custom units
     * @param precision Decimal precision
     * @return Result containing SVG string or error
     */
    suspend fun getBadge(
        chart: String? = null,
        alarm: String? = null,
        dimension: String? = null,
        after: Long? = null,
        before: Long? = null,
        group: String? = null,
        options: String? = null,
        label: String? = null,
        units: String? = null,
        precision: Int? = null
    ): Result<String> {
        return try {
            val response = service.getBadge(
                chart = chart,
                alarm = alarm,
                dimension = dimension,
                after = after,
                before = before,
                group = group,
                options = options,
                label = label,
                units = units,
                precision = precision
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get badge: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get available contexts (chart groupings)
     *
     * @return Result containing NetdataContexts or error
     */
    suspend fun getContexts(): Result<NetdataContexts> {
        return try {
            val response = service.getContexts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get contexts: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get available functions for a chart (v2 API)
     *
     * @param chart Chart ID
     * @return Result containing NetdataFunctions or error
     */
    suspend fun getFunctions(chart: String): Result<NetdataFunctions> {
        return try {
            val response = service.getFunctions(chart)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get functions: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get node/host information
     *
     * @return Result containing NetdataNode or error
     */
    suspend fun getNode(): Result<NetdataNode> {
        return try {
            val response = service.getNode()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get node info: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get chart weights/priorities
     *
     * @return Result containing NetdataWeights or error
     */
    suspend fun getWeights(): Result<NetdataWeights> {
        return try {
            val response = service.getWeights()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get weights: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
