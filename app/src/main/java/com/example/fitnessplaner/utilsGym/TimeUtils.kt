package com.example.fitnessplaner.utilsGym

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar

object TimeUtils {
    @SuppressLint("SimpleDateFormat")
    val formatter = SimpleDateFormat("mm:ss") //формат данных
    fun getTime(time:Long):String{
        val cv = Calendar.getInstance()
        cv.timeInMillis = time
        return formatter.format(cv.time)
    }
}