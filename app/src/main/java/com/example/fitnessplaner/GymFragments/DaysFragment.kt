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
import com.example.fitnessplaner.R
import com.example.fitnessplaner.adapterGym.DayModel
import com.example.fitnessplaner.adapterGym.DaysAdapter
import com.example.fitnessplaner.adapterGym.ExerciseModel
import com.example.fitnessplaner.databinding.FragmentDaysBinding
import com.example.fitnessplaner.utilsGym.DialogManager
import com.example.fitnessplaner.utilsGym.FragmentManagerGym
import com.example.fitnessplaner.utilsGym.GymViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.*


@Suppress("DEPRECATION")
class DaysFragment : Fragment(), DaysAdapter.Listener {
    private lateinit var binding: FragmentDaysBinding
    private val model: GymViewModel by activityViewModels()
    private lateinit var adapter: DaysAdapter
    private var isMenuVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Тренировки"
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.currentDay = 0
        initRcView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.reset_menu, menu)
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) { //доступ к меню
//        //return inflater.inflate(R.menu.reset_menu, menu)
//        inflater.inflate(R.menu.reset_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //очищение и обновление списка
        if (item.itemId == R.id.reset) {
            DialogManager.showDialog(activity as AppCompatActivity,
                R.string.reset_days_message,
                object : DialogManager.Listener {
                    override fun OnClick() {
                        lifecycleScope.launch {
                            model.clearProgress()
                            adapter.submitList(fillDaysArray())
                        }
                    }
                })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        // Показать меню при возвращении к фрагменту
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    override fun onPause() {
        super.onPause()
        setHasOptionsMenu(false)
        // Скрыть меню при переходе на другой фрагмент
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    private fun initRcView() = with(binding) {
        lifecycleScope.launch {
            adapter = DaysAdapter(this@DaysFragment)
            rcViewDays.layoutManager = LinearLayoutManager(activity as AppCompatActivity)
            rcViewDays.adapter = adapter

            adapter.submitList(fillDaysArray())
        }
    }

    private suspend fun fillDaysArray(): ArrayList<DayModel> = coroutineScope {
        val tArray = ArrayList<DayModel>()
        var daysDoneCounter = 0

        resources.getStringArray(R.array.exercises_day).forEach {
            model.currentDay++
            val exCounter = it.split(",").size
            val exerciseCounter = async {
                model.getExerciseCounter(model.currentDay)
            }
            val count = exerciseCounter.await()
            val dayModel = DayModel(it, exCounter == count, 0)
            tArray.add(dayModel)
        }

        binding.progressBar.max = tArray.size
        tArray.forEach{
            if (it.isDone) daysDoneCounter++
        }
        updateRestDays(tArray.size - daysDoneCounter, tArray.size)

        return@coroutineScope tArray
    }


    private fun updateRestDays(restDays:Int, days:Int) = with(binding){
        val rDays = getString(R.string.ostalos) + " $restDays " + getString(R.string.dnei)
        progressText.text = rDays
        progressBar.progress = days - restDays
    }

    private fun fillExerciseList(day: DayModel){ //взятие последовательности упражнений (разделение через ,) и взятие самих exercise в цикле
        val tempList= ArrayList<ExerciseModel>()
        day.exercises.split(",").forEach{
            val exerciseList = resources.getStringArray(R.array.exercise)
            val exercise =exerciseList[it.toInt()] //разделение на массив
            val exerciseArray = exercise.split("|")
            tempList.add(ExerciseModel(exerciseArray[0], exerciseArray[1], exerciseArray[2], false))
        }
        model.mutableListExercise.value = tempList
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment() //инициализация фрагмента (создается или получаем инстанцию)
    }

    override fun onClick(day: DayModel) {
        if (!day.isDone){
        fillExerciseList(day)
        model.currentDay = day.dayNumber
        FragmentManagerGym.setFragment(ExerciseListFragment.newInstance(), activity as AppCompatActivity)
        } else {
            DialogManager.showDialog(activity as AppCompatActivity,
                R.string.reset_day_message,
                object : DialogManager.Listener{
                    override fun OnClick() {
                        model.currentDay = day.dayNumber
                        model.savePref( model.currentDay ,0)
                        fillExerciseList(day)
                        FragmentManagerGym.setFragment(ExerciseListFragment.newInstance(), activity as AppCompatActivity)
                    }
                })
        }
    }
}