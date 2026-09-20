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

@Composable
fun ListOps(modifier: Modifier = Modifier) {

    val items = remember { List(1000) { "Item $it" } }
    val counters = remember { mutableStateMapOf<String, Int>() }

    LazyColumn(modifier = modifier) {
        // items() signature: items(items: List<T>, key: ((T) -> Any)?, itemContent: @Composable (T) -> Unit)
        // Both `item` (key selector) and `counterValue` (item content) are the same String element
        // from `items`, e.g. "Item 0". They're separate lambda params for separate lambdas that
        // Compose calls once per element - key selector picks a stable identity, item content builds the row.
        items(
            items = items,
            key = { item -> item }
        ) { counterValue ->

            val counter = counters.getOrDefault(counterValue, 0)

            MyListItem(item = counterValue, counter = counter) {
                counters[counterValue] = counter + 1
            }
        }
    }


}

@Composable
fun MyListItem(item: String, counter: Int, onIncrement: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),

        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(text = item, modifier = Modifier.weight(1f).padding(end = 8.dp))
        Button(onClick = onIncrement) {
            Text(text = "Click $counter")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListOpsPreview() {
    MaterialTheme() {ListOps() }

}
