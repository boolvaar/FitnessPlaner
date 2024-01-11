package com.example.fitnessplaner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnessplaner.NotesFragments.AllNotesFragment
import com.example.fitnessplaner.databinding.FragmentNotesBinding


class NotesFragment : Fragment() {
    private lateinit var binding:FragmentNotesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadAllNotesFragment()
    }

    private fun loadAllNotesFragment() {
        val allNotesFragment = AllNotesFragment.newInstance()
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.placeholder_notes, allNotesFragment)
        transaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotesFragment()
    }
}