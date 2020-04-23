package com.shihab.kotlintoday.feature.mvvm.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityNoteBinding
import com.shihab.kotlintoday.feature.mvvm.adapter.NoteAdapter
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.feature.mvvm.viewmodel.NoteViewModel
import com.shihab.kotlintoday.feature.mvvm.viewmodel.ViewModelFactory

class NoteActivity : AppCompatActivity() {

    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityNoteBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_note)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(
            this, ViewModelFactory(
                NoteViewModel(this)
            )
        ).get(NoteViewModel::class.java)

        viewModel.getAllNotes()

        binding.viewModel = viewModel

        viewModel.getNotes().observe(this, Observer {
            adapter = NoteAdapter(it)
            binding.recyclerNotes.setHasFixedSize(true)
            binding.recyclerNotes.adapter = adapter
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
