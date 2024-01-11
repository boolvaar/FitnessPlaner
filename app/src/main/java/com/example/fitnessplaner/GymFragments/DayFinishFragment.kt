package com.example.fitnessplaner.GymFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fitnessplaner.R
import com.example.fitnessplaner.databinding.DayFinishBinding
import com.example.fitnessplaner.utilsGym.FragmentManagerGym
import pl.droidsonroids.gif.GifDrawable

class DayFinishFragment : Fragment() {
    private lateinit var binding: DayFinishBinding
    private var ab:ActionBar? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DayFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ab = (activity as AppCompatActivity).supportActionBar
        ab?.title = getString(R.string.done)
        binding.imageView3.setImageDrawable(GifDrawable((activity as AppCompatActivity).assets, "theEnd.gif"))
        binding.button.setOnClickListener{
            FragmentManagerGym.setFragment(DaysFragment.newInstance(), activity as  AppCompatActivity)
        }
    }

    companion object {
        @JvmStatic
            fun newInstance() = DayFinishFragment() //инициализация фрагмента (создается или получаем инстанцию)
    }
}