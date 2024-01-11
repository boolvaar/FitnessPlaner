package com.example.fitnessplaner.GymFragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessplaner.R
import com.example.fitnessplaner.databinding.WaitingFragmentBinding
import com.example.fitnessplaner.utilsGym.FragmentManagerGym
import com.example.fitnessplaner.utilsGym.TimeUtils

const val COUNT_DOWN_TIME = 11000L
class WaitingFragment : Fragment() {
    private lateinit var binding: WaitingFragmentBinding
    private lateinit var timer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WaitingFragmentBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Приготовьтесь!"
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE
        binding.progressBar2.max = COUNT_DOWN_TIME.toInt()
        startTimer()
    }

    private fun startTimer() = with(binding){
        timer = object : CountDownTimer(COUNT_DOWN_TIME, 1){
            override fun onTick(restTime: Long) {
                textTimer.text = TimeUtils.getTime(restTime) //передаем в textView числа
                progressBar2.progress = restTime.toInt() //progressBar увеличивается
            }

            override fun onFinish() {
                FragmentManagerGym.setFragment(ExerciseFragment.newInstance(), activity as AppCompatActivity)
            }

        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity?)?.supportActionBar?.show()
    }

    override fun onDetach() {
        super.onDetach()
        timer.cancel()
    }

    companion object {
        @JvmStatic
        fun newInstance() = WaitingFragment() //инициализация фрагмента (создается или получаем инстанцию)
    }
}