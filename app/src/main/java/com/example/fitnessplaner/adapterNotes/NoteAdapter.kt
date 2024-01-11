package com.example.fitnessplaner.adapterNotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessplaner.NotesFragments.AddNoteFragment
import com.example.fitnessplaner.NotesFragments.AllNotesFragment
import com.example.fitnessplaner.R
import com.example.fitnessplaner.utilsNotes.FragmentManagerNotes
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class NoteAdapter(options: FirebaseRecyclerOptions<NotesModel>, private val databaseReference: AllNotesFragment,
    private val activity: AppCompatActivity) :
    FirebaseRecyclerAdapter <NotesModel, NoteAdapter.NoteViewHolder>(options){

    class NoteViewHolder (itemViewHolder: View) : RecyclerView.ViewHolder(itemViewHolder){
        private val titleTextView: TextView = itemView.findViewById(R.id.textView_title)
        private val contentTextView: TextView = itemView.findViewById(R.id.textView_content)
        private val dateTextView: TextView = itemView.findViewById(R.id.textView_date)
        private val foodTextView: TextView = itemView.findViewById(R.id.textView_food)

        fun bind(note: NotesModel) {
            titleTextView.text = note.title
            contentTextView.text = note.content
            foodTextView.text = note.food
            dateTextView.text = note.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int, model: NotesModel) {
        holder.bind(model)

        holder.itemView.setOnClickListener {
            val noteId = getItem(position)?.id
            noteId?.let { id ->
                val infoNote = AddNoteFragment.newInstance(id)
                FragmentManagerNotes.setFragment(infoNote, activity)
            }
        }
    }
}