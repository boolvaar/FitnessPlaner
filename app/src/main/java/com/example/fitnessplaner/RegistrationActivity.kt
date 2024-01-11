package com.example.fitnessplaner


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.fitnessplaner.databinding.ActivityRegistrationBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.btn.setOnClickListener {
            val email = binding.emailSignup.editText?.text.toString().trim()
            val pass = binding.passwordSignup.editText?.text.toString().trim()
            val name = binding.nameSignup.editText?.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && name.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        database = Firebase.database("https://fitnessplaner-d3fac-default-rtdb.europe-west1.firebasedatabase.app")
                        val userInfo = mutableMapOf<String, String>()
                        userInfo["email"] = binding.emailSignup.editText?.text.toString()
                        userInfo["name"] = binding.nameSignup.editText?.text.toString()
                        userInfo["profileImage"]= ""
                        userInfo["progress"] = ""
                        userInfo["notes"] = ""
                        firebaseAuth.currentUser?.let { it1 ->
                            database.reference.child("Users").child(
                                it1.uid).setValue(userInfo)
                        }

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this,"Ошибка!" ,Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, "Заполните пустые поля!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.textViewVhod.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}