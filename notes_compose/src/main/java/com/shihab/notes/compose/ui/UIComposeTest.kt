package com.shihab.notes.compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class UserPrefs(val name: String, val darkMode: Boolean)


class UICompose {

    @Preview
    @Composable
    fun singleListRow() {

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),      // children-এর মাঝে গ্যাপ
            horizontalAlignment = Alignment.CenterHorizontally      // সবগুলো মাঝখানে align
        ) {
            Text("প্রথম লাইন")
            Text("দ্বিতীয় লাইন")
            Button(onClick = {

            }) { Text("ক্লিক করো") }
        }



    }
    @Composable
    fun myCustomTextView(value: String, onValueChange: (String) -> Unit) {

        TextField(value = value, onValueChange = onValueChange)


    }

    @Composable
    fun parent() {

        var name by remember {
            mutableStateOf("")
        }

        val userSaverPref = remember {
            mapSaver(
                save = { mapOf("name" to it.name, "darkMode" to it.darkMode) },
                restore = { UserPrefs(it["name"] as String, it["darkMode"] as Boolean) }
            )
        }
        var pref by rememberSaveable(stateSaver = userSaverPref) {
            mutableStateOf(UserPrefs(name = "Shihab", darkMode = true))
        }


        val listSave = listSaver<UserPrefs, Any>(
            save = { listOf(it.name, it.darkMode) },
            restore = { UserPrefs(it[0] as String, it[1] as Boolean) }
        )


        var prefListSaver by rememberSaveable(stateSaver = listSave) {
            mutableStateOf(UserPrefs(name = "Shihab", darkMode = true))
        }
        myCustomTextView(value = name, onValueChange = {
            name = it
        })
    }


    @Preview
    @Composable
    fun previewMyCustomTextView() {
        myCustomTextView(value = "Hello", onValueChange = {})
    }
}