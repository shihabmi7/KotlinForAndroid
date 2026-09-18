package com.shihab.notes.compose.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.random.Random

/**
 * Single source of truth for the quiz. Survives configuration changes on its own
 * because the ViewModel instance itself is retained across rotation by the
 * Android framework — no rememberSaveable needed for this state.
 */
class QuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState

    fun selectAnswer(questionId: String, optionIndex: Int) {
        _uiState.update { it.copy(answers = it.answers + (questionId to optionIndex)) }
    }

    /** Advances to the next question, or into the review phase if this was the last one. */
    fun goToNext() {
        _uiState.update { state ->
            if (state.isLastQuestion) {
                state.copy(phase = QuizPhase.Review)
            } else {
                state.copy(currentIndex = state.currentIndex + 1)
            }
        }
    }

    /** Called by the per-question timer when time runs out with no manual advance. */
    fun onTimeUp() = goToNext()

    fun submit(score: Int) {
        if (_uiState.value.submission == Submission.Loading) return
        viewModelScope.launch {
            _uiState.update { it.copy(submission = Submission.Loading) }
            val result = submitScore(score)
            _uiState.update {
                it.copy(
                    submission = result.fold(
                        onSuccess = { Submission.Success },
                        onFailure = { e -> Submission.Error(e.message ?: "Unknown error") }
                    )
                )
            }
        }
    }

    /**
     * Simulates a flaky network call: runs off the main thread, takes ~1s,
     * and fails about 30% of the time. runCatching keeps the failure inside
     * a Result instead of letting it crash the coroutine/app.
     */
    private suspend fun submitScore(score: Int): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            delay(1000)
            if (Random.nextInt(100) < 30) {
                throw IOException("Failed to submit score $score — connection dropped")
            }
        }
    }
}
