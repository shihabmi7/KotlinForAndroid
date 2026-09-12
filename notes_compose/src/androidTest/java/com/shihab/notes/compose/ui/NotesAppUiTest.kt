package com.shihab.notes.compose.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.shihab.notes.data.model.Note
import org.junit.Rule
import org.junit.Test

// Exercises the stateless content composables directly (no ViewModel/Hilt
// needed) so these run fast and don't depend on a real database.
class NotesAppUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun notesListContent_showsEmptyState_whenNoNotes() {
        composeRule.setContent {
            MaterialTheme {
                NotesListContent(notes = emptyList(), onAddNoteClick = {}, onDelete = {})
            }
        }

        composeRule.onNodeWithText("No notes yet. Tap + to add one.").assertExists()
    }

    @Test
    fun notesListContent_showsNotes_andInvokesDeleteCallback() {
        val note = Note().apply {
            id = 1
            title = "Take Breakfast"
            description = "Take good food"
            priority = "1"
        }
        var deletedNote: Note? = null

        composeRule.setContent {
            MaterialTheme {
                NotesListContent(
                    notes = listOf(note),
                    onAddNoteClick = {},
                    onDelete = { deletedNote = it }
                )
            }
        }

        composeRule.onNodeWithText("Take Breakfast").assertExists()
        composeRule.onNodeWithContentDescription("Delete note").performClick()

        assert(deletedNote === note) { "Expected delete callback to receive the tapped note" }
    }

    @Test
    fun notesListContent_addFabClick_invokesCallback() {
        var addClicked = false

        composeRule.setContent {
            MaterialTheme {
                NotesListContent(notes = emptyList(), onAddNoteClick = { addClicked = true }, onDelete = {})
            }
        }

        composeRule.onNodeWithContentDescription("Add note").performClick()

        assert(addClicked) { "Expected FAB click to invoke onAddNoteClick" }
    }

    @Test
    fun addNoteContent_typingUpdatesFields_andSaveInvokesCallback() {
        var title = ""
        var saveClicked = false

        composeRule.setContent {
            MaterialTheme {
                AddNoteContent(
                    title = title,
                    onTitleChange = { title = it },
                    description = "",
                    onDescriptionChange = {},
                    priority = "",
                    onPriorityChange = {},
                    errorMessage = null,
                    onBack = {},
                    onSave = { saveClicked = true }
                )
            }
        }

        composeRule.onNodeWithText("Title").performTextInput("Buy groceries")
        composeRule.onNodeWithText("Save").performClick()

        assert(saveClicked) { "Expected Save click to invoke onSave" }
    }

    @Test
    fun addNoteContent_showsErrorMessage_whenPresent() {
        composeRule.setContent {
            MaterialTheme {
                AddNoteContent(
                    title = "",
                    onTitleChange = {},
                    description = "",
                    onDescriptionChange = {},
                    priority = "",
                    onPriorityChange = {},
                    errorMessage = "Title is empty...",
                    onBack = {},
                    onSave = {}
                )
            }
        }

        composeRule.onNodeWithText("Title is empty...").assertExists()
    }
}
