package com.shihab.notes.compose.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/** Compose-local UI state that has nothing to do with quiz progress: which review
 *  row is expanded. This — not quiz progress — is the correct use case for
 *  rememberSaveable, since it's presentation-only state owned by this screen.
 */
private data class ExpandedRowState(val expandedQuestionId: String?)

private val ExpandedRowStateSaver = mapSaver(
    save = { mapOf("expandedQuestionId" to it.expandedQuestionId) },
    restore = { ExpandedRowState(it["expandedQuestionId"] as String?) }
)

private val CorrectGreen = Color(0xFF1D9E75)
private val WrongCoral = Color(0xFFD85A30)

@Composable
fun ReviewScreen(state: QuizUiState, onSubmit: (score: Int) -> Unit) {
    var expanded by rememberSaveable(stateSaver = ExpandedRowStateSaver) {
        mutableStateOf(ExpandedRowState(null))
    }

    // Recomputes only when the actual score changes, not on every answers-map
    // mutation that happens to leave the score unchanged (e.g. switching between
    // two wrong options).
    val score by remember {
        derivedStateOf {
            state.answers.count { (questionId, selected) ->
                state.questions.find { it.id == questionId }?.correctIndex == selected
            }
        }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Same Q20/Q21 pattern as the FAB example: firstVisibleItemIndex changes on
    // every scroll pixel, but this boolean only flips when it crosses 0 — so the
    // FAB block below only recomposes at that crossing, not on every scroll frame.
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    Column(Modifier.padding(16.dp)) {
        ScoreBadge(score = score, total = state.questions.size)

        QuestionJumpRow(
            questions = state.questions,
            answers = state.answers,
            onJumpTo = { index ->
                // animateScrollToItem is a suspend fun, so it needs a coroutine —
                // this callback fires from a click, not composition, hence
                // rememberCoroutineScope() rather than LaunchedEffect (see Q28).
                coroutineScope.launch { listState.animateScrollToItem(index) }
            }
        )

        Box(Modifier.weight(1f).fillMaxWidth()) {
            LazyColumn(Modifier.fillMaxSize(), state = listState) {
                items(state.questions, key = { it.id }) { question ->
                    val isExpanded = expanded.expandedQuestionId == question.id
                    val selected = state.answers[question.id]
                    val isCorrect = selected == question.correctIndex

                    ReviewRow(
                        question = question,
                        selectedIndex = selected,
                        isCorrect = isCorrect,
                        isExpanded = isExpanded,
                        onToggle = {
                            expanded = ExpandedRowState(if (isExpanded) null else question.id)
                        }
                    )
                }
            }

            if (showScrollToTop) {
                FloatingActionButton(
                    onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Scroll to top")
                }
            }
        }

        SubmissionSection(submission = state.submission, onSubmit = { onSubmit(score) })
    }
}

@Composable
private fun QuestionJumpRow(
    questions: List<Question>,
    answers: Map<String, Int>,
    onJumpTo: (index: Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        questions.forEachIndexed { index, question ->
            val selected = answers[question.id]
            val chipColor = when {
                selected == null -> Color.Gray
                selected == question.correctIndex -> CorrectGreen
                else -> WrongCoral
            }
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(chipColor)
                    .clickable { onJumpTo(index) },
                contentAlignment = Alignment.Center
            ) {
                Text("${index + 1}", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ScoreBadge(score: Int, total: Int) {
    val ratio = if (total == 0) 0f else score / total.toFloat()
    val badgeColor by animateColorAsState(if (ratio >= 0.5f) CorrectGreen else WrongCoral)

    Card(
        backgroundColor = badgeColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Score: $score / $total",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun ReviewRow(
    question: Question,
    selectedIndex: Int?,
    isCorrect: Boolean,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val chevronRotation by animateFloatAsState(if (isExpanded) 180f else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onToggle),
        elevation = 2.dp
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = if (isCorrect) "Correct" else "Incorrect",
                        tint = if (isCorrect) CorrectGreen else WrongCoral,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(question.text, Modifier.padding(start = 8.dp))
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(chevronRotation)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(Modifier.padding(top = 8.dp)) {
                    Text(
                        "Your answer: ${selectedIndex?.let { question.options[it] } ?: "skipped"}",
                        color = if (isCorrect) CorrectGreen else WrongCoral
                    )
                    Text(
                        "Correct answer: ${question.options[question.correctIndex]}",
                        color = CorrectGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun SubmissionSection(submission: Submission, onSubmit: () -> Unit) {
    when (submission) {
        Submission.Idle -> Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth()) {
            Text("Submit score")
        }
        Submission.Loading -> Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
        Submission.Success -> Text(
            "Score submitted ✓",
            color = CorrectGreen,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        is Submission.Error -> Column {
            Text("Submission failed: ${submission.message}", color = WrongCoral)
            Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth()) { Text("Retry") }
        }
    }
}

// Sample answers: first two correct, third wrong, rest skipped — enough to see
// both the green/coral badge coloring and the check/cross icons at a glance.
private val PreviewState = QuizUiState(
    phase = QuizPhase.Review,
    answers = mapOf(
        SAMPLE_QUESTIONS[0].id to SAMPLE_QUESTIONS[0].correctIndex,
        SAMPLE_QUESTIONS[1].id to SAMPLE_QUESTIONS[1].correctIndex,
        SAMPLE_QUESTIONS[2].id to (SAMPLE_QUESTIONS[2].correctIndex + 1) % SAMPLE_QUESTIONS[2].options.size
    )
)

@Preview(showBackground = true, heightDp = 700)
@Composable
private fun ReviewScreenPreview() {
    ReviewScreen(state = PreviewState, onSubmit = {})
}

@Preview(showBackground = true, heightDp = 700)
@Composable
private fun ReviewScreenErrorPreview() {
    ReviewScreen(
        state = PreviewState.copy(submission = Submission.Error("Connection dropped")),
        onSubmit = {}
    )
}
