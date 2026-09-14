package com.shihab.notes.compose.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shihab.notes.data.model.Note
import com.shihab.notes.data.viewmodel.NoteViewModel

// Root composable, set via setContent { NotesApp() } in NoteComposeActivity.
// `hiltViewModel()` as a default param scopes the ViewModel to the nearest
// ViewModelStoreOwner (the Activity here) via Hilt, same lifecycle as the
// legacy `by viewModels()` call in the XML/View version of this screen —
// both UIs end up sharing the NoteViewModel *class*, but each gets its own
// instance per Activity, backed by the same Room DB underneath.
//
// Navigation between the two screens is a single Boolean instead of
// Navigation-Compose: with only two destinations and no back stack/deep-link
// needs, a NavHost would be pure boilerplate. Worth swapping in if a third
// screen shows up.
@Composable
fun NotesApp(viewModel: NoteViewModel = hiltViewModel()) {
    var showAddNote by remember { mutableStateOf(false) }

    MaterialTheme {
        if (showAddNote) {
            AddNoteScreen(
                viewModel = viewModel,
                onDone = { showAddNote = false }
            )
        } else {
            NotesListScreen(
                viewModel = viewModel,
                onAddNoteClick = { showAddNote = true }
            )
        }
    }
}

// Stateful: owns the ViewModel wiring. Kept thin so NotesListContent (the
// actual UI) can be previewed and tested without a ViewModel or Hilt.
@Composable
private fun NotesListScreen(viewModel: NoteViewModel, onAddNoteClick: () -> Unit) {
    // LaunchedEffect(Unit) runs once per composition (not on every
    // recomposition), which is the Compose equivalent of the legacy
    // screen's onResume() -> viewModel.getAllNotes() call.
    LaunchedEffect(Unit) { viewModel.getAllNotes() }
    // NoteViewModel exposes LiveData (shared with the View-based screen's
    // data binding), so observeAsState() bridges it into Compose state.
    // A ViewModel written for Compose from scratch would expose StateFlow
    // and use collectAsStateWithLifecycle() instead.
    val notes by viewModel.getNotes().observeAsState(emptyList())

    NotesListContent(
        notes = notes,
        onAddNoteClick = onAddNoteClick,
        onDelete = { viewModel.delete(it) }
    )
}

// Stateless/hoisted: takes data + callbacks only, no ViewModel reference.
// `internal` (not `private`) so NotesAppUiTest and the @Preview functions
// below can call it directly with fake data — no Hilt or Room needed to
// exercise this UI in isolation.
@Composable
internal fun NotesListContent(
    notes: List<Note>,
    onAddNoteClick: () -> Unit,
    onDelete: (Note) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Notes (Compose)") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNoteClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add note")
            }
        }
    ) { padding ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No notes yet. Tap + to add one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // key = note.id (the Room primary key) tells LazyColumn which
                // composable maps to which data item across recompositions,
                // so deleting one row doesn't recycle/rebind the wrong item
                // (and lets it animate as a removal instead of a full rebind).
                items(notes, key = { it.id }) { note ->
                    NoteRow(note = note, onDelete = { onDelete(note) })
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun NoteRow(note: Note, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = note.title.orEmpty(), fontWeight = FontWeight.Bold)
            Text(text = note.description.orEmpty())
            Text(text = "Priority: ${note.priority.orEmpty()}", style = MaterialTheme.typography.caption)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete note")
        }
    }
}

// Stateful: owns the ViewModel wiring and the "save succeeded -> go back"
// side effect. AddNoteContent below is the previewable/testable UI.
@Composable
private fun AddNoteScreen(viewModel: NoteViewModel, onDone: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val message by viewModel.message.observeAsState()

    // LaunchedEffect keyed on `message` re-runs only when the LiveData value
    // actually changes, so it fires once per save/validation attempt rather
    // than on every recomposition (e.g. every keystroke in the fields above).
    //
    // Matching on the exact success string is fragile (it's UI logic
    // reaching into what's meant to be a status message) — it works only
    // because it's ported as-is from the legacy NoteViewModel, which was
    // never designed with a "did it succeed" signal Compose could observe
    // directly. A cleaner fix would be a sealed SaveResult (Success/Error)
    // exposed by the ViewModel instead of a raw String.
    LaunchedEffect(message) {
        when (message) {
            "Successfully Inserted" -> {
                viewModel.clearMessage()
                onDone()
            }
            null -> Unit
            else -> {
                errorMessage = message
                viewModel.clearMessage()
            }
        }
    }

    AddNoteContent(
        title = title,
        onTitleChange = { title = it },
        description = description,
        onDescriptionChange = { description = it },
        priority = priority,
        onPriorityChange = { priority = it },
        errorMessage = errorMessage,
        onBack = onDone,
        onSave = {
            viewModel.note.title = title
            viewModel.note.description = description
            viewModel.note.priority = priority
            viewModel.saveNote()
        }
    )
}

// Same stateless/hoisted pattern as NotesListContent: field values and the
// error message are owned by the caller (AddNoteScreen) and passed in, so
// this composable itself is pure UI and trivially previewable/testable.
@Composable
internal fun AddNoteContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    priority: String,
    onPriorityChange: (String) -> Unit,
    errorMessage: String?,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Note") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = priority,
                onValueChange = onPriorityChange,
                label = { Text("Priority") },
                modifier = Modifier.fillMaxWidth()
            )
            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colors.error)
            }
            Button(
                onClick = onSave,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesListContentPreview() {
    MaterialTheme {
        NotesListContent(
            notes = listOf(
                Note().apply { id = 1; title = "Take Breakfast"; description = "Take good food"; priority = "1" },
                Note().apply { id = 2; title = "Start Work"; description = "Start Work"; priority = "1" }
            ),
            onAddNoteClick = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesListContentEmptyPreview() {
    MaterialTheme {
        NotesListContent(notes = emptyList(), onAddNoteClick = {}, onDelete = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun AddNoteContentPreview() {
    MaterialTheme {
        AddNoteContent(
            title = "Buy groceries",
            onTitleChange = {},
            description = "Milk, eggs, bread",
            onDescriptionChange = {},
            priority = "2",
            onPriorityChange = {},
            errorMessage = null,
            onBack = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddNoteContentErrorPreview() {
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
