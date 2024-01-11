package com.example.fitnessplaner.adapterGym

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessplaner.R
import com.example.fitnessplaner.databinding.DaysListItemBinding

class DaysAdapter(var listener:Listener):ListAdapter<DayModel, DaysAdapter.DayHolder>(MyComparator()) {
    class DayHolder(view:View) : RecyclerView.ViewHolder(view){
        private val binding = DaysListItemBinding.bind(view)
        fun setData(day:DayModel, listener: Listener) = with(binding){ //можем напрямую писать идентификатор т.к. в функции подключили binding
            val name = root.context.getString(R.string.day) + "${adapterPosition+1}" //позиция адаптера (с 0)
            textViewName.text = name
            val exCounter = day.exercises.split(",").size.toString() + " "+root.context.getString(R.string.exercise) //узнаем количество упражнений
            textViewCounter.text = exCounter
            checkBox.isChecked = day.isDone
            itemView.setOnClickListener { listener.onClick(day.copy(dayNumber = adapterPosition+1)) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolder { //создание элемента
        val view = LayoutInflater.from(parent.context).inflate(R.layout.days_list_item, parent, false)
        return DayHolder(view)
    }

    override fun onBindViewHolder(holder: DayHolder, position: Int) { //заполнение
        holder.setData(getItem(position), listener)
    }
    class MyComparator:DiffUtil.ItemCallback<DayModel>(){ //перерисовка или не перерисовка элемента в зависимости от того одинаковые эл-ты или нет
        override fun areItemsTheSame(oldItem: DayModel, newItem: DayModel): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: DayModel, newItem: DayModel): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener{
        fun onClick(day: DayModel)
    }
}
