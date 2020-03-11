package com.shihab.kotlintoday.feature.mvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityNoteBinding
import com.shihab.kotlintoday.feature.mvvm.viewmodel.ViewModelFactory
import com.shihab.kotlintoday.feature.mvvm.adapter.NoteAdapter
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.feature.mvvm.viewmodel.NoteViewModel

class NoteActivity : AppCompatActivity() {


    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NoteAdapter
    var notes = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityNoteBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_note)

        val factory =
            ViewModelFactory(
                NoteViewModel(this)
            )
        viewModel = ViewModelProviders.of(this, factory).get(NoteViewModel::class.java)

        binding.viewModel = viewModel

        viewModel.getAllNotes().observe(this, Observer {

            adapter = NoteAdapter(it)
            binding.recyclerNotes.setHasFixedSize(true)
            binding.recyclerNotes.adapter = adapter
        })


    }
}
