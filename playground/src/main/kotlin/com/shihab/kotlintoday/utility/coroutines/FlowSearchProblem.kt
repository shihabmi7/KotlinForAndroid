package com.shihab.kotlintoday.utility.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
 * PROBLEM 6 — Search-as-you-type, the Flow way
 *
 * Problem 1 solved debouncing manually with Job cancellation. Solve the SAME
 * idea again here, but idiomatically with Flow operators instead.
 *
 * Given queryFlow (raw keystrokes), build searchResults() as a Flow that:
 *   - debounces keystrokes by 300ms (only proceeds once typing pauses)
 *   - ignores a query if it's identical to the last one actually searched
 *   - for each surviving query, calls performSearch(query) and emits its result
 *   - if a NEW query arrives before the in-flight performSearch() for the
 *     previous one finishes, that stale search must be cancelled — its
 *     result must never be emitted
 *
 * Relevant operators: debounce(), distinctUntilChanged(), mapLatest()
 *   (mapLatest automatically cancels its previous transform block when a new
 *   upstream value arrives before that block finishes — that's what gives you
 *   "cancel stale in-flight work" for free, no manual Job bookkeeping needed)
 *
 * main() emits the same fast burst as Problem 1 ("K","Ko","Kot","Kotl","Kotlin"),
 * then a DUPLICATE "Kotlin" (should be ignored as a repeat of the last search),
 * then a pause, then "Coroutines".
 * Expected: exactly two "Result for: ..." lines total — "Kotlin" and
 * "Coroutines". The duplicate "Kotlin" must NOT trigger a second search.
 */

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
fun searchResults(queryFlow: Flow<String>): Flow<String> {
    TODO("debounce(300) -> distinctUntilChanged() -> mapLatest { performSearchApi(it) }")
}

// Named differently from Problem 1's performSearch() (same package, and Kotlin
// doesn't allow overloading by return type alone — Unit vs String would clash).
suspend fun performSearchApi(query: String): String {
    println("Searching for: $query")
    delay(150) // simulate the search itself taking some time
    return "Result for: $query"
}

fun main() = runBlocking {
    val queryFlow = MutableSharedFlow<String>(extraBufferCapacity = 10)

    launch {
        searchResults(queryFlow).collect { result ->
            println(result)
        }
    }

    delay(10) // let the collector above actually subscribe before we emit

    val burst = listOf("K", "Ko", "Kot", "Kotl", "Kotlin", "Kotlin") // note: duplicate "Kotlin"
    for (q in burst) {
        queryFlow.emit(q)
        delay(50)
    }

    delay(500) // let the debounced + mapLatest search actually complete
    println("--- pause ---")

    queryFlow.emit("Coroutines")
    delay(500)
}
