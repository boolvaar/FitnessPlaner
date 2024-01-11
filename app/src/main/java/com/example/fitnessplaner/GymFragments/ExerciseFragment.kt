package com.example.fitnessplaner.GymFragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.fitnessplaner.R
import com.example.fitnessplaner.adapterGym.ExerciseModel
import com.example.fitnessplaner.databinding.ExerciseBinding
import com.example.fitnessplaner.utilsGym.FragmentManagerGym
import com.example.fitnessplaner.utilsGym.GymViewModel
import com.example.fitnessplaner.utilsGym.TimeUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifDrawable

@Suppress("DEPRECATION")
class ExerciseFragment : Fragment() {
    private var timer:CountDownTimer? = null
    private lateinit var binding: ExerciseBinding
    private var exerciseCounter: Int = 0
    private var exList:ArrayList<ExerciseModel>? = null
    private var currentDay = 0
    private val model: GymViewModel by activityViewModels()
    private var ab:ActionBar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        lifecycleScope.launch {
            val counter = async {
                model.getExerciseCounter(model.currentDay)
            }
            exerciseCounter = counter.await()
            ab = (activity as AppCompatActivity).supportActionBar
            model.mutableListExercise.observe(viewLifecycleOwner){
                exList = it
                nextExercise()
            }
            binding.bNext.setOnClickListener{
                nextExercise()
            }
        }
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
                FragmentManagerGym.setFragment(ExerciseListFragment.newInstance(), activity as AppCompatActivity)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun nextExercise(){
        Log.d("ExerciseFragment", "ExerciseCounter from DB: ${exerciseCounter}")
        if (exerciseCounter < exList?.size!!) {
            val ex = exList?.get(exerciseCounter++) ?: return
            currentDay = model.currentDay
            model.savePref(currentDay ,exerciseCounter-1)
            showExercise(ex)
            setExerciseType(ex)
            showNextExercise()
        } else {
            currentDay = model.currentDay
            exerciseCounter++
            model.savePref(currentDay ,exerciseCounter-1)

            FragmentManagerGym.setFragment(DayFinishFragment.newInstance(), activity as AppCompatActivity)
        }
    }

    private fun showExercise(exercise:ExerciseModel) = with(binding){
        imMain.setImageDrawable(GifDrawable(root.context.assets, exercise.image))
        tvName.text = exercise.name
        val title = "$exerciseCounter / ${exList?.size}"
        ab?.title = title
    }

    private fun setExerciseType(exercise: ExerciseModel){
        if (exercise.time.startsWith("x")){
            binding.progressBar3.max = 10
            binding.progressBar3.progress = 10
            timer?.cancel()
            binding.tvTime.text = exercise.time
        }else{
            startTimer(exercise)
        }
    }

    private fun startTimer(exercise: ExerciseModel) = with(binding){
        progressBar3.max = exercise.time.toInt()*1000
        timer?.cancel()
        timer = object : CountDownTimer(exercise.time.toLong()*1000, 1){
            override fun onTick(restTime: Long) {
                tvTime.text = TimeUtils.getTime(restTime) //передаем в textView числа
                progressBar3.progress = restTime.toInt() //progressBar увеличивается
            }
            override fun onFinish() {
                nextExercise()
            }
        }.start()
    }

    override fun onDetach() {
        super.onDetach()
        timer?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Восстанавливаем видимость нижней панели навигации при уничтожении фрагмента
        (activity as AppCompatActivity?)?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }



    private fun showNextExercise() = with(binding){
        if (exerciseCounter < exList?.size!!){
            val ex = exList?.get(exerciseCounter) ?: return
            imNext.setImageDrawable(GifDrawable(root.context.assets, ex.image))
            setTimeType(ex)
        } else{
            imNext.setImageDrawable(GifDrawable(root.context.assets, "theEnd.gif"))
            tvNextName.text = getString(R.string.done)
            ab?.title = getString(R.string.done)
        }
    }

    private fun setTimeType(ex:ExerciseModel){
        if (ex.time.startsWith("x")){
            val nameX = ex.name +": " + ex.time
            binding.tvNextName.text = nameX
        }else{
            val name = ex.name + ": ${TimeUtils.getTime(ex.time.toLong()*1000)}"
            binding.tvNextName.text = name
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExerciseFragment() //инициализация фрагмента (создается или получаем инстанцию)
    }
}