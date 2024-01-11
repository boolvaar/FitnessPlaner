package com.example.fitnessplaner.NotesFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.fitnessplaner.R
import com.example.fitnessplaner.adapterNotes.NotesModel
import com.example.fitnessplaner.databinding.FragmentAddNoteBinding
import com.example.fitnessplaner.utilsNotes.FragmentManagerNotes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
class AddNoteFragment : Fragment() {
    private lateinit var binding: FragmentAddNoteBinding
    private lateinit var database: FirebaseDatabase
    private var noteId: String? = null
    private var isEditText:Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            noteId = it.getString("NOTE_ID")
        }
        Log.d("AddNoteFragment", "onCreate - isEditText: $isEditText, noteId: $noteId")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE

        Log.d("AddNoteFragment", "isEditText: $isEditText, noteId: $noteId")

        if (noteId != null && noteId!!.isNotEmpty()){
            isEditText = true
            loadNoteFromFirebase(noteId!!)
        }

        Log.d("AddNoteFragment", "After loading note, isEditText: $isEditText, noteId: $noteId")

        if (isEditText){
            binding.textViewAddNew.text = "Отредактируйте заметку!"
            binding.textViewDelete.visibility = View.VISIBLE
        }
        binding.textViewDelete.setOnClickListener{
            deleteNoteFromFirebase()
        }
        binding.saveBtn.setOnClickListener{
            if (isTitleNotEmpty()) {
                saveNote()
                FragmentManagerNotes.setFragment(AllNotesFragment.newInstance(), activity as AppCompatActivity)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_back, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                FragmentManagerNotes.setFragment(AllNotesFragment.newInstance(), activity as AppCompatActivity)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity?)?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }


    private fun isTitleNotEmpty(): Boolean {
        val title = binding.editTitle.text.toString().trim()
        return if (title.isEmpty()) {
            binding.editTitle.error = "Заполните поле"
            false
        } else {
            true
        }
    }

    private fun loadNoteFromFirebase(noteId: String) {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            database = Firebase.database("https://fitnessplaner-d3fac-default-rtdb.europe-west1.firebasedatabase.app")
            val notesRef: DatabaseReference = database.getReference("Users").child(user.uid).child("notes").child(noteId)

            notesRef.addListenerForSingleValueEvent(object : ValueEventListener { //слушатель для однократного получения данных
                override fun onDataChange(snapshot: DataSnapshot) { //вызывается когда данные в бд меняются
                    if (snapshot.exists()) { //текущее состояние (сущ. ли данные в узле)
                        val loadedNote = snapshot.getValue(NotesModel::class.java)
                        binding.editTitle.setText(loadedNote?.title)
                        binding.editContent.setText(loadedNote?.content)
                        binding.editFood.setText(loadedNote?.food)
                    } else {
                        Toast.makeText(context, "Заметка не найдена", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Ошибка при загрузке заметки", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun deleteNoteFromFirebase() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            database =
                Firebase.database("https://fitnessplaner-d3fac-default-rtdb.europe-west1.firebasedatabase.app")
            val notesRef: DatabaseReference =
                database.getReference("Users").child(user.uid).child("notes")

            context?.let { ctx ->
                notesRef.child(noteId!!).removeValue().addOnCompleteListener { task -> //слушатель для отслеживания завершения
                    // операции удаления (task результат операции)
                    if (task.isSuccessful) {
                        Toast.makeText(ctx, "Заметка удалена!", Toast.LENGTH_SHORT).show()
                        FragmentManagerNotes.setFragment(AllNotesFragment.newInstance(), activity as AppCompatActivity)
                    } else {
                        Toast.makeText(ctx, "Ошибка при удалении заметки", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } ?: run {
            }
        }
    }

    private fun saveNote(){
        val title = binding.editTitle.text.toString()
        val content = binding.editContent.text.toString()
        val date = getCurrentDate()
        val food = binding.editFood.text.toString()
        if (title.isEmpty()){
            binding.editTitle.error = "Заполните поле!"
            return
        }
        val myNote = NotesModel(null, title, food, content, date)
        saveNoteToFirebase(myNote)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private fun saveNoteToFirebase(note: NotesModel) {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            database = Firebase.database("https://fitnessplaner-d3fac-default-rtdb.europe-west1.firebasedatabase.app")
            val notesRef: DatabaseReference = database.getReference("Users").child(user.uid).child("notes") //ref путь к узлу
            if (isEditText && noteId != null) {
                // редактирование существующей заметки
                note.id = noteId
                context?.let { ctx ->
                    notesRef.child(noteId!!).setValue(note).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(ctx, "Заметка обновлена!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(ctx, "Ошибка при обновлении заметки", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } ?: run { }
            } else {
                val noteKey = notesRef.push().key
                if (noteKey != null) {
                    note.id = noteKey
                    context?.let { ctx ->
                        notesRef.child(noteKey).setValue(note).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(ctx, "Заметка добавлена!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(ctx, "Ошибка при добавлении заметки", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } ?: run {}
                } else { }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddNoteFragment()

        fun newInstance(noteId: String?) = AddNoteFragment().apply {
            arguments = Bundle().apply {
                putString("NOTE_ID", noteId)
            }
        }
    }
}