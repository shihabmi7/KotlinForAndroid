package com.shihab.kotlintoday.utility.coroutines

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select

/*
 * PROBLEM 4 — Race for the first SUCCESS (like Promise.any, not Promise.race)
 *
 * You're calling 3 mirror servers for the same data. Implement raceFirstSuccess
 * so that:
 *   - All 3 blocks are launched concurrently, immediately.
 *   - As soon as ANY one of them completes successfully, return its result
 *     and CANCEL the other two still-running blocks (don't leave them running).
 *   - If a block throws, that's not a "win" — ignore that failure and keep
 *     waiting on the others.
 *   - If ALL blocks fail, throw an exception (your choice which one/what message).
 *
 * This is harder than a plain "first one to finish wins" race, because a fast
 * FAILURE must not end the race — only a fast SUCCESS should.
 *
 * main() runs 3 mirrors: mirrorA fails fast (100ms), mirrorB succeeds slowly
 * (800ms), mirrorC succeeds fastest among the successful ones (400ms).
 * Expected: your function returns mirrorC's result (~400ms), NOT mirrorA's
 * failure, and mirrorB's still-running call gets cancelled instead of also
 * printing its "done" message afterward.
 *
 * SOLVED — verified output: "Winner: mirrorC result", and mirrorB's "should
 * NOT print" line never appears (confirms cancellation actually worked).
 * Key idea: wrap each block in runCatching{} INSIDE the async{} so a failing
 * block completes its Deferred normally (holding Result.failure) instead of
 * making the async itself fail — otherwise structured concurrency's default
 * "one child fails -> cancel siblings" would kill the race the instant
 * mirrorA throws. Then select{ onAwait } picks off whichever pending
 * Deferred finishes next; only a Result.success cancels the rest and wins.
 */

suspend fun <T> raceFirstSuccess(vararg blocks: suspend () -> T): T = coroutineScope {
    // Wrap each block in runCatching so a failing block completes its async
    // NORMALLY (holding Result.failure) instead of actually throwing — that
    // sidesteps structured concurrency's "one child fails -> cancel siblings"
    // default, which would otherwise kill the race the instant mirrorA fails.
    val pending = blocks.map { block -> async { runCatching { block() } } }.toMutableList()

    while (pending.isNotEmpty()) {
        // select{onAwait} resolves as soon as ANY pending deferred completes,
        // regardless of which one — exactly "wait for the next one to finish".
        val (finished, result) = select<Pair<Deferred<Result<T>>, Result<T>>> {
            pending.forEach { d -> d.onAwait { r -> d to r } }
        }
        pending.remove(finished)

        result.onSuccess { value ->
            pending.forEach { it.cancel() } // cancel every still-running loser
            return@coroutineScope value
        }
        // on failure: just loop again with this one removed from `pending`
    }

    error("raceFirstSuccess: all ${blocks.size} blocks failed")
}

suspend fun mirrorA(): String {
    delay(100)
    throw RuntimeException("mirrorA: server unreachable")
}

suspend fun mirrorB(): String {
    delay(800)
    println("mirrorB finished (should NOT print if cancellation works correctly)")
    return "mirrorB result"
}

suspend fun mirrorC(): String {
    delay(400)
    return "mirrorC result"
}

fun main() = runBlocking {
    val winner = raceFirstSuccess(
        { mirrorA() },
        { mirrorB() },
        { mirrorC() }
    )
    println("Winner: $winner")
    delay(600) // give a cancelled mirrorB a chance to wrongly print, if it wasn't cancelled
}
