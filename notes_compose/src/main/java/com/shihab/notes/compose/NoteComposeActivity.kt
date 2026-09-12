package com.shihab.notes.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.shihab.notes.compose.ui.NotesApp
import dagger.hilt.android.AndroidEntryPoint

// Compose entry point for the Notes feature, launched from HomeActivity as
// a normal exported=false Activity (not a dynamic-feature module) so it can
// be referenced with a plain Intent(this, NoteComposeActivity::class.java)
// like every other screen in the app.
//
// ComponentActivity (not AppCompatActivity) is enough here: this screen has
// no Views/XML/menus/action bar to bridge, so it doesn't need AppCompat's
// View-system compatibility shims — setContent {} + Compose is the whole UI.
// @AndroidEntryPoint is still required for Hilt to inject into it (that's
// how NotesApp()'s default `hiltViewModel()` param finds a component to
// resolve NoteViewModel from).
@AndroidEntryPoint
class NoteComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesApp()
        }
    }
}
