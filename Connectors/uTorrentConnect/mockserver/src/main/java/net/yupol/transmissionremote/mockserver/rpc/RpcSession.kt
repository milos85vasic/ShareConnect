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


package net.yupol.transmissionremote.mockserver.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RpcSession(
    @SerialName("speed-limit-down") var speedLimitDown: Int = 100,
    @SerialName("speed-limit-down-enabled") var isSpeedLimitDownEnabled: Boolean = false,
    @SerialName("speed-limit-up") var speedLimitUp: Int = 50,
    @SerialName("speed-limit-up-enabled") var isSpeedLimitUpEnabled: Boolean = false,
    @SerialName("alt-speed-down") var altSpeedLimitDown: Int = 512,
    @SerialName("alt-speed-up") var altSpeedLimitUp: Int = 0,
    @SerialName("alt-speed-enabled") var isAltSpeedLimitEnabled: Boolean = false,
    @SerialName("download-dir") var downloadDir: String = "~/Downloads",
    @SerialName("seedRatioLimited") var isSeedRatioLimited: Boolean = false,
    @SerialName("seedRatioLimit") var seedRatioLimit: Double = 2.0,
    @SerialName("idle-seeding-limit-enabled") var isSeedIdleLimited: Boolean = false,
    @SerialName("idle-seeding-limit") var seedIdleLimit: Int = 30,
    @SerialName("download-dir-free-space") var downloadDirFreeSpace: Long = 77022388224,
    @SerialName("version") val version: String = "4.0.4 (24077e3511)"
) : RpcArguments
