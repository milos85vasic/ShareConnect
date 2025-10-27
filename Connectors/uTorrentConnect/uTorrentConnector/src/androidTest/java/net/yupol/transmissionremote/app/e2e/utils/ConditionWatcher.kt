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


package com.shareconnect.utorrentconnect.e2e.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ConditionWatcher(
    private val timeoutLimit: Duration = 15.seconds,
    private val watchInterval: Duration = 250.milliseconds
) {
    @Throws(Exception::class)
    fun waitForCondition(description: String, condition: Condition) {
        var status = Status.CONDITION_NOT_MET
        var elapsedTime = Duration.ZERO
        do {
            if (condition.check()) {
                status = Status.CONDITION_MET
            } else {
                elapsedTime += watchInterval
                Thread.sleep(watchInterval.inWholeMilliseconds)
            }
            if (elapsedTime >= timeoutLimit) {
                status = Status.TIMEOUT
                break
            }
        } while (status != Status.CONDITION_MET)

        if (status == Status.TIMEOUT) {
            throw IllegalStateException(
                "'${description}' - took more than " + timeoutLimit.inWholeMilliseconds + " milliseconds",
                condition.exception
            )
        }
    }

    abstract class Condition {

        var exception: Throwable? = null
            private set

        @Throws(Throwable::class)
        abstract fun checkCondition()

        fun check(): Boolean {
            return try {
                checkCondition()
                true
            } catch (e: Throwable) {
                exception = e
                false
            }
        }
    }

    private enum class Status {
        CONDITION_NOT_MET, CONDITION_MET, TIMEOUT
    }
}
