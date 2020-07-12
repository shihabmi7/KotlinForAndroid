package com.shihab.kotlintoday.feature.mvvm.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityNoteBinding
import com.shihab.kotlintoday.feature.mvvm.adapter.NoteAdapter
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.feature.mvvm.viewmodel.NoteViewModel
import com.shihab.kotlintoday.feature.mvvm.viewmodel.ViewModelFactory
import com.shihab.kotlintoday.rest.RetrofitClient
import com.shihab.kotlintoday.utility.LogMe
import kotlinx.coroutines.*

class NoteActivity : AppCompatActivity() {

    val TAG = NoteActivity::class.java.name

    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NoteAdapter
    lateinit var binding: ActivityNoteBinding

    lateinit var job: Job
    var listFromServer: List<Note> = mutableListOf()
    lateinit var noteListFromDB: List<Note>
    lateinit var noteList: List<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_note)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(
            this, ViewModelFactory(
                NoteViewModel(this)
            )
        ).get(NoteViewModel::class.java)

        binding.viewModel = viewModel
        binding.recyclerNotes.setHasFixedSize(true)

        /*viewModel.getNotes().observe(this, Observer {
            adapter = NoteAdapter(it)
            binding.recyclerNotes.adapter = adapter
            viewModel.isLoading.set(false)
        })*/

        //getNotesCallOnMainThread(binding)
        //getNotesWithoutMVVM()
        handleACoroutineLifecycle()

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

    private fun setRecyclerAdapter(
        response: List<Note>
    ) {
        adapter = NoteAdapter(response)
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
        GlobalScope.launch(Dispatchers.Main) { // launch & Async is the starter of coroutine

            // launch is fire and forget concept
            // By Default launch use > Dispatcher.Default
            // Dispatchers.Main must use when need to update UI !!

            viewModel.isLoading.set(true)
            val list = async { getNotesFromServer() }.await()
            adapter = NoteAdapter(list)
            binding.recyclerNotes.adapter = adapter
            viewModel.isLoading.set(false)
        }
    }

    fun handleACoroutineLifecycle() {
        job = GlobalScope.launch(Dispatchers.IO) {
            listFromServer = async { RetrofitClient.getAPIInterface().getNotes() }.await()
        }

        GlobalScope.async (job + Dispatchers.Main) {
            // noteListFromDB = db.getDatabase()
            setRecyclerAdapter(listFromServer)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job!!.cancel()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
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
}