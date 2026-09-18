package com.shihab.kotlintoday.utility.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/*
 * PROBLEM 2 — Retry with exponential backoff
 *
 * Implement retryWithBackoff so that:
 *   - It calls `block()` up to `times` attempts total.
 *   - If `block()` throws, wait before retrying: the wait doubles each time
 *     starting from `initialDelayMs` (i.e. 1st retry waits initialDelayMs,
 *     2nd retry waits initialDelayMs*2, 3rd waits initialDelayMs*4, ...).
 *   - If the final attempt also fails, rethrow that last exception —
 *     don't swallow it.
 *   - If any attempt succeeds, return its result immediately (no more waiting).
 *
 * flakyApiCall() below fails on its first 2 calls, then succeeds on the 3rd.
 * With times = 5 and initialDelayMs = 100, your solution should succeed on
 * attempt 3 after two waits (100ms, then 200ms).
 *
 * Bonus (optional, don't overthink it for the first pass): try adding a cap
 * so the delay never exceeds some maxDelayMs, even after many retries.
 *
 * SOLVED — verified output:
 *   Attempt #1
 *   Attempt #2
 *   Attempt #3
 *   Final result: Success on attempt #3
 */

suspend fun <T> retryWithBackoff(
    times: Int,
    initialDelayMs: Long,
    factor: Double = 2.0,
    block: suspend (attempt: Int) -> T
): T {
    repeat(times) { attempt ->
        try {
            return block(attempt)
        } catch (e: Exception) {
            if (attempt == times - 1) {
                throw e // rethrow the last exception if all attempts fail
            }
            val delayTime = (initialDelayMs * Math.pow(factor, attempt.toDouble())).toLong()
            delay(delayTime)
        }
    }

    // Unreachable in practice: every iteration above either returns (success)
    // or throws (on the last attempt). error(...) has return type Nothing, so
    // it satisfies T for the compiler's flow analysis, which can't otherwise
    // prove the repeat{} block always exits via one of those two paths.
    error("retryWithBackoff: exhausted attempts without returning or throwing")
}

private var attemptCount = 0

suspend fun flakyApiCall(): String {
    attemptCount++
    println("Attempt #$attemptCount")
    if (attemptCount < 3) {
        throw RuntimeException("Simulated network failure on attempt #$attemptCount")
    }
    return "Success on attempt #$attemptCount"
}

fun main() = runBlocking {
    val result = retryWithBackoff(times = 5, initialDelayMs = 100) { flakyApiCall() }
    println("Final result: $result")
}
