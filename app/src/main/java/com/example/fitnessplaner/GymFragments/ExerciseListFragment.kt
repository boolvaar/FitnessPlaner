package com.example.fitnessplaner.GymFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnessplaner.NotesFragments.AllNotesFragment
import com.example.fitnessplaner.R
import com.example.fitnessplaner.adapterGym.ExerciseAdapter
import com.example.fitnessplaner.databinding.ExerciseListFragmentBinding
import com.example.fitnessplaner.utilsGym.FragmentManagerGym
import com.example.fitnessplaner.utilsGym.GymViewModel
import com.example.fitnessplaner.utilsNotes.FragmentManagerNotes
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class ExerciseListFragment : Fragment() {
    private lateinit var binding:ExerciseListFragmentBinding
    private lateinit var adapter: ExerciseAdapter
    private val model: GymViewModel by activityViewModels()
    private var counter:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ExerciseListFragmentBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Тренировки"
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_back, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                FragmentManagerGym.setFragment(DaysFragment.newInstance(), activity as AppCompatActivity)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE
        init()

        val currentDay = model.currentDay

        lifecycleScope.launch {
            val exerciseCounter = async {
                model.getExerciseCounter(currentDay)
            }
            counter = exerciseCounter.await()

            val count = exerciseCounter.await()

            model.mutableListExercise.observe(viewLifecycleOwner) { list ->
                for (i in 0 until count) {
                    if (i < list.size) {
                        list[i] = list[i].copy(isDone = true)
                    }
                }
                adapter.submitList(list)
            }
        }
    }

    private fun init() = with(binding){
        adapter = ExerciseAdapter()
        rcView.layoutManager = LinearLayoutManager(activity)
        rcView.adapter = adapter
        buttonStart.setOnClickListener{
            FragmentManagerGym.setFragment(WaitingFragment.newInstance(), activity as  AppCompatActivity)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExerciseListFragment() //инициализация фрагмента (создается или получаем инстанцию)
    }
}
