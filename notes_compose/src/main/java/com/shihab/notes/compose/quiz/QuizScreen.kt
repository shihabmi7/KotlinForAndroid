package com.shihab.notes.compose.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

private object QuizDestinations {
    const val QUIZ = "quiz"
    const val REVIEW = "review"
}

/**
 * Stateful entry point: collects the ViewModel and drives a NavHost with two
 * routes. The ViewModel's `phase` is still the single source of truth (it's
 * what survives rotation) — this LaunchedEffect just keeps the NavController's
 * back stack in sync with it.
 *
 * Honest caveat: quiz -> review only ever moves forward, so a plain
 * `when (state.phase)` switch (see NotesApp.kt's Screen enum for that pattern)
 * would genuinely be simpler for this app. NavHost earns its keep here for one
 * real reason: `popUpTo(QUIZ) { inclusive = true }` removes the quiz route
 * from the back stack entirely, so pressing system back from the review screen
 * exits the app instead of re-entering a finished quiz's last question.
 */
@Composable
fun QuizRoute(viewModel: QuizViewModel = viewModel()) {
    val state by viewModel.uiState.observeAsStateCompat()
    val navController = rememberNavController()

    LaunchedEffect(state.phase) {
        if (state.phase == QuizPhase.Review) {
            navController.navigate(QuizDestinations.REVIEW) {
                popUpTo(QuizDestinations.QUIZ) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = QuizDestinations.QUIZ) {
        composable(QuizDestinations.QUIZ) {
            QuizInProgressScreen(
                state = state,
                onSelectAnswer = viewModel::selectAnswer,
                onNext = viewModel::goToNext,
                onTimeUp = viewModel::onTimeUp
            )
        }
        composable(QuizDestinations.REVIEW) {
            ReviewScreen(
                state = state,
                onSubmit = { score -> viewModel.submit(score) }
            )
        }
    }
}

/**
 * StateFlow -> Compose State bridge. lifecycle-runtime-compose's collectAsStateWithLifecycle
 * is the production-grade choice, but this module's older Compose/lifecycle versions don't
 * carry that artifact, so this is the minimal equivalent: collect in a lifecycle-scoped effect.
 */
@Composable
private fun <T> StateFlow<T>.observeAsStateCompat(): State<T> {
    val state = remember { mutableStateOf(value) }
    LaunchedEffect(this) {
        collect { state.value = it }
    }
    return state
}

@Composable
private fun QuizInProgressScreen(
    state: QuizUiState,
    onSelectAnswer: (questionId: String, optionIndex: Int) -> Unit,
    onNext: () -> Unit,
    onTimeUp: () -> Unit
) {
    val question = state.currentQuestion ?: return

    // DisposableEffect-backed: true while the app is in the foreground, false
    // while backgrounded. Read (not observed) from inside the timer coroutine
    // below so the countdown pauses instead of silently burning time while
    // the user is away from the app.
    val isForeground by rememberIsAppInForeground()

    // Keyed on currentIndex: switching questions cancels the previous countdown
    // automatically and starts a fresh one. No manual Job bookkeeping needed —
    // this IS structured concurrency, not just a phrase.
    var secondsLeft by remember(state.currentIndex) { mutableStateOf(TIMER_SECONDS) }
    LaunchedEffect(state.currentIndex) {
        secondsLeft = TIMER_SECONDS
        while (secondsLeft > 0) {
            if (isForeground) {
                delay(1000)
                secondsLeft--
            } else {
                delay(200) // idle-poll while backgrounded; the countdown itself is paused
            }
        }
        onTimeUp()
    }

    Column(Modifier.padding(16.dp)) {
        Text("Question ${state.currentIndex + 1} / ${state.questions.size}")
        LinearProgressIndicator(
            progress = secondsLeft / TIMER_SECONDS.toFloat(),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        Text(if (isForeground) "${secondsLeft}s left" else "Paused — app in background")

        QuestionCard(
            question = question,
            selectedIndex = state.answers[question.id],
            onSelect = { index -> onSelectAnswer(question.id, index) }
        )

        Button(
            onClick = onNext,
            enabled = state.answers.containsKey(question.id),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(if (state.isLastQuestion) "Finish" else "Next")
        }
    }
}

/**
 * Stateless: this is the state-hoisting pattern. QuestionCard owns no state of its
 * own — it renders whatever it's given and reports selection via a callback.
 */
@Composable
fun QuestionCard(
    question: Question,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit
) {
    Card(Modifier.padding(vertical = 16.dp).fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(question.text)
            question.options.forEachIndexed { index, option ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(selected = index == selectedIndex, onClick = { onSelect(index) })
                ) {
                    RadioButton(selected = index == selectedIndex, onClick = null)
                    Text(option, Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionCardPreview() {
    QuestionCard(question = SAMPLE_QUESTIONS.first(), selectedIndex = 1, onSelect = {})
}
