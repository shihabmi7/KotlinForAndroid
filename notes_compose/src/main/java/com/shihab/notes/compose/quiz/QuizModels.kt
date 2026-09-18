package com.shihab.notes.compose.quiz

/**
 * Plain data model for the quiz feature. Deliberately dependency-free (no Compose,
 * no Android) so it can be unit tested and reused outside the UI layer.
 */
data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)

val SAMPLE_QUESTIONS = listOf(
    Question(
        id = "q1",
        text = "What triggers recomposition of a composable function?",
        options = listOf(
            "Calling invalidate() manually",
            "A State object read within the composable changing value",
            "The Activity's onResume() being called",
            "Any variable inside the composable changing"
        ),
        correctIndex = 1
    ),
    Question(
        id = "q2",
        text = "What does rememberSaveable add on top of remember?",
        options = listOf(
            "Faster recomposition",
            "Survives configuration change and process death via the saved-instance Bundle",
            "Makes the value observable across screens",
            "Nothing, they're identical"
        ),
        correctIndex = 1
    ),
    Question(
        id = "q3",
        text = "What does async return in Kotlin coroutines?",
        options = listOf(
            "Job",
            "Deferred<T>, awaited via .await()",
            "Flow<T>",
            "The raw result directly"
        ),
        correctIndex = 1
    ),
    Question(
        id = "q4",
        text = "Why is Dispatchers.IO preferred over Dispatchers.Default for network calls?",
        options = listOf(
            "IO is faster at math",
            "IO uses a larger, elastic thread pool suited to blocking I/O work",
            "Default cannot run suspend functions",
            "There is no difference"
        ),
        correctIndex = 1
    ),
    Question(
        id = "q5",
        text = "What does derivedStateOf optimize for?",
        options = listOf(
            "Persisting state to disk",
            "Skipping recomposition when a computed value doesn't actually change, even if its inputs change often",
            "Making state thread-safe",
            "Replacing LaunchedEffect"
        ),
        correctIndex = 1
    ),
    Question(
        id = "q6",
        text = "What happens to a LaunchedEffect's coroutine when its key changes?",
        options = listOf(
            "It keeps running alongside the new one",
            "The previous coroutine is cancelled and a new one is launched",
            "It throws an exception",
            "Nothing, keys only affect logging"
        ),
        correctIndex = 1
    ),
    Question(
        id = "q7",
        text = "Why must key() be used in a LazyColumn's items() for a mutable list?",
        options = listOf(
            "It's required for the code to compile",
            "It gives Compose a stable identity for each item across mutations, preserving state correctly",
            "It sorts the list",
            "It sets the accessibility content description"
        ),
        correctIndex = 1
    ),
    Question(
        id = "q8",
        text = "With a regular (non-supervisor) coroutineScope, what happens to sibling children if one throws?",
        options = listOf(
            "Nothing, siblings finish normally",
            "The exception propagates and cancels the other siblings too",
            "The exception is silently ignored",
            "Only the app's UI thread is affected"
        ),
        correctIndex = 1
    )
)

const val TIMER_SECONDS = 20

sealed interface Submission {
    object Idle : Submission
    object Loading : Submission
    object Success : Submission
    data class Error(val message: String) : Submission
}

enum class QuizPhase { InProgress, Review }

data class QuizUiState(
    val questions: List<Question> = SAMPLE_QUESTIONS,
    val currentIndex: Int = 0,
    val answers: Map<String, Int> = emptyMap(),
    val phase: QuizPhase = QuizPhase.InProgress,
    val submission: Submission = Submission.Idle
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex
}
