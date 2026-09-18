package com.shihab.kotlintoday.utility

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

/*
 * PRACTICE PROBLEM 1 (live-coding style):
 *
 * Create a function that runs two API calls in parallel.
 * - If an API call takes more than 2 seconds, return null instead of failing.
 * - If an API call throws an exception, immediately propagate the error.
 *
 * fetchFirstData() below can time out (it sleeps 3s > 2s limit).
 * fetchSecondData() below can throw (simulate a real failure).
 *
 * Fill in fetchInParallel() so `main()` prints something sensible for both cases.
 *
 * SOLVED — verified output: "(null, second data)" (timeout case). Also
 * verified separately: swapping fetchSecondData's return for a thrown
 * exception correctly crashes main() with that exception propagating
 * straight through await(), instead of being swallowed.
 */

suspend fun fetchInParallel(): Pair<String?, String> = coroutineScope {
    // Both launched immediately via async — they run concurrently, not
    // sequentially, since neither is awaited until both are already started.
    val first = async {
        withTimeoutOrNull(2000) { fetchFirstData() } // null instead of throwing on timeout
    }
    val second = async {
        fetchSecondData() // any exception here propagates straight out of await()
    }

    Pair(first.await(), second.await())
}

suspend fun fetchFirstData(): String {
    delay(3000) // intentionally exceeds the 2s timeout
    return "first data"
}

suspend fun fetchSecondData(): String {
    delay(500)
    return "second data"
    // try swapping the line above for: throw IllegalStateException("network error")
    // to verify your solution actually propagates exceptions instead of swallowing them
}

fun main() = runBlocking {
    println(fetchInParallel())
}
