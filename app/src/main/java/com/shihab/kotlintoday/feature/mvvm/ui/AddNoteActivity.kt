package com.shihab.kotlintoday.feature.mvvm.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityAddNoteBinding
import com.shihab.kotlintoday.feature.mvvm.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteActivity : AppCompatActivity() {

    val viewModel: NoteViewModel by viewModels()
    lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_add_note
        )
        title = "Add Note!"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.viewModel = viewModel

        viewModel.message.observe(this, {
            if (it.isNotEmpty())
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save) {
            binding.viewModel?.saveNote()
        } else if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
