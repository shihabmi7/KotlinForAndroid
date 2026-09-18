package com.shihab.kotlintoday.utility

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/*
 * PRACTICE PROBLEM 2 (live-coding style):
 *
 * Note: this is a plain JVM module (no Android runtime), so Dispatchers.Main
 * doesn't exist here the way it would in the app module — use Dispatchers.Default
 * as its stand-in for this exercise. The interview question itself asked for
 * Dispatchers.Main; know that substitution and say so out loud if this comes up
 * for real.
 *
 * Question 1: Call callbackApi.fetchData on the "main" thread (Dispatchers.Default
 *             here), wrapping the callback into a suspend value with suspendCoroutine.
 * Question 2: Collect the resulting Flow on a background thread (Dispatchers.IO).
 *
 * SOLVED — verified output: "Result: Hello, world!"
 */

interface CallbackApi {
    fun fetchData(callback: (String?, Throwable?) -> Unit)
}

val callbackApi = object : CallbackApi {
    override fun fetchData(callback: (String?, Throwable?) -> Unit) {
        Thread.sleep(1000) // simulates a slow async call
        callback("Hello, world!", null)
        // try swapping the line above for: callback(null, RuntimeException("boom"))
        // to verify your suspendCoroutine wiring propagates the failure correctly
    }
}

class ApiWrapper(private val api: CallbackApi) {

    // Question 1: wrap api.fetchData's callback into a suspend call via
    // suspendCoroutine, run it on Dispatchers.Default, and emit the result.
    fun fetchData(): Flow<String> = flow {
        val result = withContext(Dispatchers.Default) {
            suspendCoroutine<String> { continuation ->
                api.fetchData { success, error ->
                    when {
                        success != null -> continuation.resume(success)
                        error != null -> continuation.resumeWithException(error)
                        else -> continuation.resumeWithException(NullPointerException("Data null"))
                    }
                }
            }
        }
        emit(result)
    }

    // Question 2: collect fetchData() on a background thread.
    suspend fun test() {
        withContext(Dispatchers.IO) {
            fetchData().collect { result ->
                println("Result: $result")
            }
        }
    }
}

fun main() = runBlocking {
    ApiWrapper(callbackApi).test()
}
