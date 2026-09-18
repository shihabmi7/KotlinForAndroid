package com.shihab.kotlintoday.utility.coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.atomic.AtomicInteger

/*
 * PROBLEM 3 — Bounded concurrency (rate-limited downloads)
 *
 * You have 12 "download" tasks. Running all 12 fully in parallel would be
 * too aggressive on a real server — implement downloadAll so AT MOST 3 tasks
 * run concurrently at any moment, while still using coroutines (no manual
 * threads). The other 9 should queue and start as soon as a slot frees up.
 *
 * Hint: look at kotlinx.coroutines.sync.Semaphore — but figure out where
 * exactly to acquire/release it yourself.
 *
 * main() tracks `activeCount` with an AtomicInteger and prints the peak
 * concurrent count it ever observed. Your solution is correct if the
 * printed peak is exactly 3 (never higher), and all 12 tasks still complete.
 *
 * SOLVED — verified output: 4 clean batches of 3 (active count cycles
 * 1,2,3, 1,2,3, ...), "All done: 12 files", "Peak concurrency observed: 3".
 * Key fix: launch one async per id (12 tasks total), and put the
 * semaphore.withPermit{} around the downloadFile call INSIDE each task —
 * not one async per permit slot (that duplicates work instead of sharing it).
 */

private val activeCount = AtomicInteger(0)
private val peakConcurrency = AtomicInteger(0)

suspend fun downloadFile(fileName: String, cooldownMs: Long = 200): String {
    val current = activeCount.incrementAndGet()
    peakConcurrency.updateAndGet { maxOf(it, current) }
    println("Started download: $fileName (currently active: $current)")

    delay(300) // simulate network time
    println("Finished download: $fileName, cooling down ${cooldownMs}ms before freeing its slot")
    delay(cooldownMs) // slot stays occupied during cooldown — next task waits a bit longer

    activeCount.decrementAndGet()
    return "$fileName downloaded"
}

suspend fun downloadAll(fileNames: List<String>, maxConcurrent: Int): List<String> = coroutineScope {
    // One permit per allowed slot. acquire() suspends (doesn't block a thread)
    // when all permits are taken, resuming as soon as another task releases one.
    val semaphore = Semaphore(permits = maxConcurrent)

    // Launch ALL 12 tasks immediately — the throttling happens inside each
    // task (around downloadFile), not by limiting how many tasks get created.
    fileNames.map { name ->
        async {
            semaphore.withPermit {
                downloadFile(name)
            }
        }
    }.awaitAll()
}

fun main() = runBlocking {
    val fileNames = listOf(
        "invoice.pdf", "profile.jpg", "report.docx", "song.mp3",
        "video.mp4", "backup.zip", "presentation.pptx", "spreadsheet.xlsx",
        "notes.txt", "wallpaper.png", "resume.pdf", "podcast.mp3"
    )
    val results = downloadAll(fileNames, maxConcurrent = 3)
    println("All done: ${results.size} files")
    println("Peak concurrency observed: ${peakConcurrency.get()} (should be exactly 3)")
}
