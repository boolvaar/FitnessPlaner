package com.example.fitnessplaner

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.fitnessplaner.GymFragments.DaysFragment
import com.example.fitnessplaner.databinding.FragmentGymBinding
import com.example.fitnessplaner.utilsGym.GymViewModel


class GymFragment : Fragment() {
    private lateinit var binding: FragmentGymBinding
    private val model: GymViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGymBinding.inflate(inflater, container, false)
        model.pref = requireActivity().getSharedPreferences("main", MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDaysFragment()
    }

    private fun loadDaysFragment() {
        val daysFragment = DaysFragment.newInstance()
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.placeholder_gym, daysFragment)
        transaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = GymFragment()
    }
}