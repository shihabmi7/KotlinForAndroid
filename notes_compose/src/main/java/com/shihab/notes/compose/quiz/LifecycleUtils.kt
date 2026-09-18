package com.shihab.notes.compose.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * The DisposableEffect example promised earlier: this isn't coroutine-based work
 * (no suspend calls, no delay loop) — it's registering a callback-style listener
 * (LifecycleEventObserver) that must be explicitly unregistered, which is exactly
 * what LaunchedEffect is NOT built for and DisposableEffect is.
 */
@Composable
fun rememberIsAppInForeground(): State<Boolean> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val isForeground = remember { mutableStateOf(true) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> isForeground.value = true
                Lifecycle.Event.ON_STOP -> isForeground.value = false
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    return isForeground
}
