package com.shihab.kotlintoday.feature.mvvm.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityNoteBinding
import com.shihab.kotlintoday.feature.mvvm.adapter.NoteAdapter
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.feature.mvvm.viewmodel.NoteViewModel
import com.shihab.kotlintoday.rest.RetrofitClient
import com.shihab.kotlintoday.utility.LogMe
import com.shihab.kotlintoday.utility.ShowToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


@AndroidEntryPoint
class NoteActivity : AppCompatActivity() {
    lateinit var adapter: NoteAdapter
    lateinit var binding: ActivityNoteBinding
    lateinit var job: Job
    lateinit var noteListFromDB: List<Note>
    lateinit var noteList: List<Note>
    val TAG = NoteActivity::class.java.name
    val viewModel: NoteViewModel by viewModels()
    var listFromServer: List<Note> = mutableListOf()
    val globalScope = CoroutineScope(Dispatchers.Main)
    val ioScope = CoroutineScope(Dispatchers.IO)

    val networkJob = ioScope.launch {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_note)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.viewModel = viewModel
        adapter = NoteAdapter()
        binding.recyclerNotes.adapter = adapter

        viewModel.getNotes().observe(this, {
            adapter.addNotes(it)
        })

        viewModel.isAddNotesClicked.observe(this, {
            openAddNoteActivity()
        })

        lifecycleScope.launchWhenResumed {
            viewModel.getAllNotes()
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.delete(adapter.getNote(viewHolder.absoluteAdapterPosition))
                Toast.makeText(viewHolder.itemView.context, "Note deleted", Toast.LENGTH_SHORT)
                    .show()
            }
        }).attachToRecyclerView(binding.recyclerNotes)
        //getNotesCallOnMainThread(binding)
        //getNotesWithoutMVVM()
        //handleACoroutineLifecycle()

    }

    private fun getNotesCallOnMainThread() {
        /**  Call Network on MainThread : Crash Expected*/
        try {
            viewModel.isLoading.set(true)
            val response = RetrofitClient.getAPIInterface().getNotesRaw()
            setRecyclerAdapter(response)
            viewModel.isLoading.set(false)
            LogMe.e(TAG, "Server Notes List : " + response.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setRecyclerAdapter(response: List<Note>) {
        adapter = NoteAdapter()
        adapter.addNotes(response)
        binding.recyclerNotes.adapter = adapter
    }

    private suspend fun getNotesFromServer(): List<Note> {
        /**  Call Network on MainThread : Crash Expected*/
        lateinit var response: List<Note>
        try {
            coroutineScope {

                /**this will also works*/
                response = RetrofitClient.getAPIInterface().getNotes()

                /** suggested way*/
                /*response = withContext(Dispatchers.IO) {
                    RetrofitClient.getAPIInterface().getNotes()
                }*/

                // asyncronous call
                // response =  async(Dispatchers.IO) { RetrofitClient.getAPIInterface().getNotes() }.await()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    fun getNotesWithoutMVVM() {
        /** We have 3 Dispatcher
         * Dispatcher.Main > should use update Ui and small task
         * Dispatcher.IO > Network & Database
         * Dispatcher.Default > CPU intensive task (GPU rendering)
         * */
        GlobalScope.launch(Dispatchers.Main + exceptionHandler) { // launch & Async is the starter of coroutine

            // launch is fire and forget concept
            // By Default launch use > Dispatcher.Default
            // Dispatchers.Main must use when need to update UI !!

            viewModel.isLoading.set(true)
            val list = async { getNotesFromServer() }.await()
            adapter = NoteAdapter()
            adapter.addNotes(list)
            binding.recyclerNotes.adapter = adapter
            viewModel.isLoading.set(false)
        }
    }

    private fun handleACoroutineLifecycle() {
        job = GlobalScope.launch(Dispatchers.IO) {
            listFromServer = async { RetrofitClient.getAPIInterface().getNotes() }.await()
        }

        GlobalScope.async(job + Dispatchers.Main) {
            // noteListFromDB = db.getDatabase()
            setRecyclerAdapter(listFromServer)
        }
    }

    private fun openAddNoteActivity() {
        startActivity(Intent(this, AddNoteActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        //job.cancel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun doLongRunningTask() {
        withContext(Dispatchers.Default) {
            // your code for doing a long running task
            // Added delay to simulate
            delay(5000)
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        ShowToast(exception.localizedMessage)
    }
}