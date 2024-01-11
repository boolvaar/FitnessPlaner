package com.example.fitnessplaner.utilsGym

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitnessplaner.adapterGym.ExerciseModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GymViewModel : ViewModel() {
    val database: FirebaseDatabase = Firebase.database("https://fitnessplaner-d3fac-default-rtdb.europe-west1.firebasedatabase.app")
    val mutableListExercise = MutableLiveData<ArrayList<ExerciseModel>>()
    var pref: SharedPreferences? = null
    var currentDay = 0 //какой день в данный момент открыт


    fun savePref(day: Int, value: Int) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val exerciseRef = database.reference.child("Users").child(currentUser?.uid ?: "").child("progress")
            .child(currentDay.toString())
        exerciseRef.setValue(value).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("GymViewModel", "Data saved successfully")
            } else {
                Log.e("GymViewModel", "Error saving data: ${task.exception}")
            }
        }
    }

    fun clearProgress() {
        val currentUser:FirebaseUser? = FirebaseAuth.getInstance().currentUser
        database.reference.child("Users").child(currentUser?.uid?: "").child("progress").removeValue()

    }

    suspend fun getExerciseCounter(currentDay: Int): Int {
        return suspendCoroutine { continuation ->
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            val exerciseRef = database.reference.child("Users").child(currentUser?.uid ?: "")
                .child("progress").child(currentDay.toString())

            Log.d("GymViewModel", "Querying exercise count for day: $currentDay")

            exerciseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val countValue = snapshot.getValue(Long::class.java)
                    val count = countValue?.toInt() ?: 0
                    Log.d("GymViewModel", "Exercise count for day $currentDay: $count")
                    continuation.resume(count)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("GymViewModel", "Error querying exercise count: ${error.message}")
                    continuation.resume(0)
                }
            })
        }
    }
}

