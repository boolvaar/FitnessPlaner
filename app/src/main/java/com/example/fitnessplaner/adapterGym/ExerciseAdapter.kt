package com.example.fitnessplaner.adapterGym

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessplaner.R
import com.example.fitnessplaner.databinding.ExerciseListItemBinding
import pl.droidsonroids.gif.GifDrawable

class ExerciseAdapter():ListAdapter<ExerciseModel, ExerciseAdapter.ExerciseHolder>(MyComparator()) {
    class ExerciseHolder(view:View) : RecyclerView.ViewHolder(view){
        private val binding = ExerciseListItemBinding.bind(view)

        fun setData(exercise:ExerciseModel) = with(binding){ //можем напрямую писать идентификатор т.к. в функции подключили binding
            textName.text = exercise.name
            textCount.text = exercise.time
            imEx.setImageDrawable(GifDrawable(root.context.assets, exercise.image))
            chB.isChecked = exercise.isDone
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder { //создание элемента
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_list_item, parent, false)
        return ExerciseHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseHolder, position: Int) { //заполнение
        holder.setData(getItem(position))
    }
    class MyComparator:DiffUtil.ItemCallback<ExerciseModel>(){ //перерисовка или не перерисовка элемента в зависимости от того одинаковые эл-ты или нет
        override fun areItemsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem == newItem
        }
    }

}