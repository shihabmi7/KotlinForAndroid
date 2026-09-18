package com.shihab.notes.compose.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * Entry point for the timed quiz demo, launched from HomeActivity like every
 * other screen in the app. No Hilt needed here — QuizViewModel has no
 * injected dependencies, so the default viewModel() factory is enough.
 */
class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizRoute()
        }
    }
}
