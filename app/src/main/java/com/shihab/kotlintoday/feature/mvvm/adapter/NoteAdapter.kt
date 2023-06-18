package com.shihab.kotlintoday.feature.mvvm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ItemNoteBinding
import com.shihab.kotlintoday.feature.mvvm.model.Note

import java.util.*

class NoteAdapter : Adapter<NoteAdapter.NoteHolder>() {

    private val notes: MutableList<Note> = ArrayList()

    class NoteHolder(val itemview: ItemNoteBinding) : RecyclerView.ViewHolder(itemview.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
       /* val itemview =
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
     */
        val itemview =
            ItemNoteBinding.inflate(LayoutInflater.from(parent.context))

        return NoteHolder(itemview)
    }

    override fun getItemCount() = notes.size

    fun addNotes(list: List<Note>) {
        notes.clear()
        notes.addAll(list)
        notifyDataSetChanged()
    }

    fun getNote(position: Int): Note {
        return notes[position]
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val note = notes[position]
        holder.itemview.textViewTitle.text = note.title
        holder.itemview.textViewDescription.text = note.description
        holder.itemview.textViewPriority.text = note.priority.toString()
    }
}