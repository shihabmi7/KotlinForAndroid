package com.shihab.notes.compose.interview

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * INTERVIEW NOTES — "Why is this LazyColumn written this way?"
 *
 * The question this snippet is usually used to probe: given a 1000-row list where
 * each row has its own per-item state (a click counter), what stops that state from
 * getting scrambled or reset while the user scrolls?
 *
 * 1. `remember { List(1000) { "Item $it" } }`
 *    Without `remember`, a fresh 1000-item list would be allocated on every
 *    recomposition of `ExpensiveList`. `remember` caches it across recompositions
 *    (but NOT across a config change/process death — that would need
 *    `rememberSaveable` or state hoisted to a ViewModel).
 *
 * 2. State is lifted OUT of `ExpensiveItem` and into a single
 *    `mutableStateMapOf<String, Int>` keyed by item id, owned by the parent.
 *    `LazyColumn` recycles composition slots as you scroll — it does not keep one
 *    `remember` per row alive forever. If each `ExpensiveItem` tried to hold its own
 *    `var counter by remember { mutableStateOf(0) }`, that counter would live in the
 *    *slot*, not the *item* — scroll far enough for the slot to be reused by a
 *    different item and the counter appears to "jump" to a stale value. Hoisting the
 *    counters into a map keyed by stable item id, and reading via
 *    `counters.getOrDefault(item, 0)`, ties the value to the *data*, not the slot.
 *
 * 3. `key = { item -> item }` on `items(...)`.
 *    This is the other half of the fix above. The key tells Compose which composable
 *    instance corresponds to which data element across recompositions, insertions,
 *    deletions, and reordering. Without it, Compose falls back to matching by
 *    position, which is exactly what causes the "wrong row updates" bug when the
 *    list changes shape (e.g. after a delete/filter/sort).
 *
 * 4. Only `mutableStateMapOf` is observed as state — mutating one entry
 *    (`counters[item] = counter + 1`) only recomposes the row(s) reading that entry,
 *    not the entire 1000-item list. This is what actually makes the list "cheap" —
 *    the name is a bit of a misnomer: nothing here is expensive by default, the
 *    sample is testing whether *you* keep it that way as state is added.
 *
 * Common follow-up asked in interviews: "What if `item` (the String) weren't unique?"
 *  — Using a non-unique value as both the map key and the `key =` lambda would merge
 *    distinct rows together. In a real app the key should be a stable, unique id from
 *    the domain model (e.g. `note.id`), not a derived/display string.
 */
@Composable
fun ExpensiveList(modifier: Modifier = Modifier) {
    // Create the items list only once when the composable enters composition
    val items = remember { List(1000) { "Item $it" } }

    // Lift state up to maintain counter values across recycling
    // Use a mutable state map to store counter values for each item
    val counters = remember { mutableStateMapOf<String, Int>() }

    LazyColumn(modifier = modifier) {
        // `item` in `key = { item -> ... }` and `item` in the trailing content lambda are two
        // separate lambda params (key selector vs item content) — Compose calls each once per
        // element of `items`, and for a given row both refer to the same String, e.g. "Item 0".
        items(
            items = items,
            key = { item -> item }
        ) { item ->
            // Get current counter value from the map, defaulting to 0 if not present
            val counter = counters.getOrDefault(item, 0)

            ExpensiveItem(
                item = item,
                counter = counter,
                onIncrement = { counters[item] = counter + 1 }
            )
        }
    }
}

@Composable
fun ExpensiveItem(
    item: String,
    counter: Int,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = item,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        Button(onClick = onIncrement) {
            Text("Click $counter")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpensiveListPreview() {
    MaterialTheme {
        ExpensiveList()
    }
}
