package com.shihab.kotlintoday.feature.mvvm.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityAddNoteBinding
import com.shihab.kotlintoday.feature.mvvm.viewmodel.NoteViewModel
import com.shihab.kotlintoday.feature.mvvm.viewmodel.ViewModelFactory

class AddNoteActivity : AppCompatActivity() {

    lateinit var viewModel: NoteViewModel
    lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_add_note
        )

        title = "Add Note!"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory =
            ViewModelFactory(
                NoteViewModel(this)
            )
        viewModel = ViewModelProviders.of(this, factory).get(NoteViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.message.observe(this, Observer {
            if (it.isNotEmpty())
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_note, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_save) {
            binding.viewModel?.saveNote()
        } else if (item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
