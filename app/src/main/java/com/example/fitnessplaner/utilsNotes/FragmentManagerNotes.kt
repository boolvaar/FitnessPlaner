package com.example.fitnessplaner.utilsNotes

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fitnessplaner.R

object FragmentManagerNotes {
    var currentFragment: Fragment? = null

    fun setFragment(newFragment: Fragment, act: AppCompatActivity){
        val transaction = act.supportFragmentManager.beginTransaction()//есть функции, где заменяем 1 фрагмент на другой
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        transaction.replace(R.id.placeholder_notes, newFragment) //заменяем фрагменты
        transaction.commit() //переключаемся между фрагментами
        currentFragment = newFragment

    }

}