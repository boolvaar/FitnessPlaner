package com.example.fitnessplaner.NotesFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessplaner.adapterNotes.NoteAdapter
import com.example.fitnessplaner.adapterNotes.NotesModel
import com.example.fitnessplaner.databinding.FragmentAllNotesBinding
import com.example.fitnessplaner.utilsNotes.FragmentManagerNotes
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

@Suppress("DEPRECATION")
class AllNotesFragment : Fragment() {
    private lateinit var binding: FragmentAllNotesBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllNotesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAdd.setOnClickListener {
            FragmentManagerNotes.setFragment(
                AddNoteFragment.newInstance(),
                requireActivity() as AppCompatActivity
            )
        }
        recyclerView = binding.recyclerView
        setUpRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun getCollectionReferenceForNotes(): DatabaseReference {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        database =
            FirebaseDatabase.getInstance("https://fitnessplaner-d3fac-default-rtdb.europe-west1.firebasedatabase.app")

        return if (currentUser != null) {
            database.reference.child("Users").child(currentUser.uid).child("notes")
        } else {
            throw IllegalStateException("User not authenticated")
        }
    }

    private fun setUpRecyclerView() {
        val query: Query = getCollectionReferenceForNotes().orderByChild("date")

        query.addChildEventListener(object : ChildEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // Обработка добавления заметки
                noteAdapter.notifyDataSetChanged()
                checkAndSetVisibility()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Обработка изменения заметки
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Обработка удаления заметки
                noteAdapter.notifyDataSetChanged()
                checkAndSetVisibility()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Обработка перемещения заметки
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибок
            }
        })

        val options: FirebaseRecyclerOptions<NotesModel> =
            FirebaseRecyclerOptions.Builder<NotesModel>()
                .setQuery(query, NotesModel::class.java).build()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        noteAdapter = NoteAdapter(options, this, activity as AppCompatActivity)
        recyclerView.adapter = noteAdapter
    }

    private fun checkAndSetVisibility() {
        if (binding.recyclerView.isEmpty()) {
            binding.textView7.visibility = View.GONE
        } else {
            binding.textView7.visibility = View.VISIBLE
        }
    }


    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter.stopListening()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        noteAdapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AllNotesFragment()
    }
}
