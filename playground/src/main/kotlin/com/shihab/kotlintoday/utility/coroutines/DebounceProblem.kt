package com.shihab.kotlintoday.utility.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
 * PROBLEM 1 — Debounced search
 *
 * A search box calls `onQueryChanged(query)` on every keystroke. Real network
 * calls should only fire once the user has PAUSED typing for 300ms — every
 * keystroke before that should cancel the previous pending call.
 *
 * Implement Debouncer so that:
 *   - Calling submit(query) schedules a search after 300ms.
 *   - If submit(...) is called again before those 300ms pass, the previous
 *     pending search is cancelled and the timer restarts for the new query.
 *   - Only queries that survive the full 300ms quiet period actually call
 *     performSearch(query).
 *
 * main() below simulates fast typing ("K", "Ko", "Kot", "Kotl", "Kotlin")
 * with only 50ms between keystrokes, then a pause, then one more query.
 * Expected behavior: performSearch should fire exactly TWICE total —
 * once for "Kotlin" (the last of the fast burst) and once for the final query.
 *
 * SOLVED — verified output:
 *   Searching for: Kotlin
 *   --- pause ---
 *   Searching for: Coroutines
 */

class Debouncer(private val scope: CoroutineScope, private val delayMs: Long = 300) {

    private var pendingSearch: kotlinx.coroutines.Job? = null

    // Cancel-and-relaunch pattern: cancelling a Job that's suspended inside
    // delay() stops it from ever reaching the line after the delay, so only
    // the LAST call to submit() before a quiet period survives to search.
    fun submit(query: String) {
        pendingSearch?.cancel()
        pendingSearch = scope.launch {
            delay(delayMs)
            performSearch(query)
        }
    }
}

suspend fun performSearch(query: String) {
    println("Searching for: $query")
}

fun main() = runBlocking {
    val debouncer = Debouncer(this)

    // 50ms < the 300ms debounce window on purpose: each keystroke cancels the
    // previous one before its delay finishes, so only "Kotlin" (the last of
    // the burst) survives long enough to actually search.
    val burst = listOf("K", "Ko", "Kot", "Kotl", "Kotlin")
    for (q in burst) {
        debouncer.submit(q)
        delay(50)
    }

    // 500ms > the 300ms window: gives the pending "Kotlin" search time to
    // actually complete before main() moves on (runBlocking cancels any
    // still-pending child coroutines the instant it returns).
    delay(500)
    println("--- pause ---")

    debouncer.submit("Coroutines")
    delay(500) // same reason: let this last search finish before main() exits
}
