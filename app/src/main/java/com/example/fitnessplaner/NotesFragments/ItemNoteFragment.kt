package com.example.fitnessplaner.NotesFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnessplaner.R
import com.example.fitnessplaner.databinding.FragmentItemNoteBinding

class ItemNoteFragment : Fragment() {
    private lateinit var binding:FragmentItemNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ItemNoteFragment()
    }
}