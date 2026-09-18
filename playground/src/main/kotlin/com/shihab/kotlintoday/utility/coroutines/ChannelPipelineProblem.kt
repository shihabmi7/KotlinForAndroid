package com.shihab.kotlintoday.utility.coroutines

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
 * PROBLEM 5 — Fan-out producer/consumer pipeline with a Channel
 *
 * Build a pipeline that:
 *   - A single PRODUCER coroutine sends the numbers 1..20 into a Channel<Int>,
 *     then closes the channel when done.
 *   - THREE CONSUMER coroutines all read from the same channel concurrently
 *     (fan-out — each number should be picked up by exactly ONE consumer,
 *     not all three), square the number, and print
 *     "Consumer <id> processed <n> -> <n*n>".
 *   - runPipeline() should suspend until the producer AND all three consumers
 *     have finished, then return.
 *
 * Correctness checks when you run it:
 *   - Every number 1..20 should be printed exactly once total (across all
 *     consumers combined) — not 20 times per consumer, not skipped.
 *   - You should see a MIX of consumer ids in the output (not all 20 lines
 *     from consumer 1 only) — that's the fan-out actually working, since a
 *     Channel unlike a Flow gives EACH element to only ONE receiver.
 *   - The program must actually terminate (no coroutine hangs waiting on a
 *     channel that was never closed).
 */

suspend fun runPipeline(): Unit = coroutineScope {
    val channel = Channel<Int>()

//    TODO("launch 1 producer coroutine that sends 1..20 into `channel` then closes it, " +
//        "and 3 consumer coroutines (ids 1, 2, 3) that receive from the same channel " +
//        "until it's closed, printing each result")result
    launch {
        for (i in 1..20) {
            channel.send(i)
        }
        channel.close()
    }

    for (id in 1..3) {
        launch {
            for (n in channel) {
                println("Consumer $id processed $n -> ${n * n}")
            }
        }
    }

}

fun main() = runBlocking {
    runPipeline()
    println("Pipeline finished.")
}
