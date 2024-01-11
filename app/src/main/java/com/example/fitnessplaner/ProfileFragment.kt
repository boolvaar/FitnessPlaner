package com.example.fitnessplaner

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.stream.MediaStoreImageThumbLoader
import com.example.fitnessplaner.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var filePath:Uri
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        (activity as AppCompatActivity?)?.findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        loadUserInfo()

        binding.imgProfile.setOnClickListener{
            selectImage()
        }

        binding.imageButtonExit.setOnClickListener{
            saveProfileData()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.imageButtonSave.setOnClickListener {
            saveProfileData()
            checkProfileStatus()
            Toast.makeText(requireContext(), "Данные сохранены успешно!", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    var pickImageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null && result.data!!.data != null){
            filePath = result.data!!.data!!
            try {
                val source:ImageDecoder.Source = ImageDecoder.createSource(requireContext().contentResolver, filePath)
                val bitmap:Bitmap = ImageDecoder.decodeBitmap(source)
                binding.imgProfile.setImageBitmap(bitmap)
            }catch (e : IOException){
                e.printStackTrace()
            }
            uploadImage()
        }
    }

    private fun saveProfileData(){
        FirebaseAuth.getInstance().currentUser?.let {
            database.reference.child("Users").child(it.uid).apply {
                child("age").setValue(binding.editAge.text.toString())
                child("currentWeight").setValue(binding.editCurrentWeight.text.toString())
                child("wantWeight").setValue(binding.editWantWeight.text.toString())
                val genderId = binding.radioGroup.checkedRadioButtonId
                if (genderId == R.id.radioButton_man){
                    child("gender").setValue("Мужской")
                }else if (genderId == R.id.radioButton_woman){
                    child("gender").setValue("Женский")
                }
            }
        }
    }

    private fun loadUserInfo(){
        FirebaseAuth.getInstance().currentUser?.let {
            database = Firebase.database("https://fitnessplaner-d3fac-default-rtdb.europe-west1.firebasedatabase.app")
            database.reference.child("Users").child(
                it.uid).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username:String = snapshot.child("name").value.toString()
                    val profileImage:String = snapshot.child("profileImage").value.toString()
                    val age: String? = snapshot.child("age").value?.toString()
                    val gender: String = snapshot.child("gender").value.toString()
                    val currentWeight: String? = snapshot.child("currentWeight").value?.toString()
                    val wantWeight : String? = snapshot.child("wantWeight").value?.toString()
                    binding.userName.text = username
                    if (!age.isNullOrEmpty()){
                        binding.editAge.setText(age)
                    }
                    if (!currentWeight.isNullOrEmpty()){
                        binding.editCurrentWeight.setText(currentWeight)
                    }
                    if (!wantWeight.isNullOrEmpty()) {
                        binding.editWantWeight.setText(wantWeight)
                    }
                    if (gender == "Мужской") {
                        binding.radioButtonMan.isChecked = true
                    } else if (gender == "Женский") {
                        binding.radioButtonWoman.isChecked = true
                    }
                    if (profileImage.isNotEmpty()){
                        context?.let { it1 -> Glide.with(it1).load(profileImage).into(binding.imgProfile) }
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
            checkProfileStatus()
        }
    }


    private fun checkProfileStatus() {
        FirebaseAuth.getInstance().currentUser?.let {
            database.reference.child("Users").child(it.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isProfileEmpty = binding.editAge.text.isEmpty() ||
                            binding.editCurrentWeight.text.isEmpty() ||
                            binding.editWantWeight.text.isEmpty() ||
                            binding.radioGroup.isEmpty()

                    binding.textViewProf.text = if (isProfileEmpty) "Заполните профиль" else " Ваш профиль"
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun selectImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        pickImageActivityResultLauncher.launch(intent)
    }

    private fun uploadImage(){
        if (filePath!=null){
            database = Firebase.database("https://fitnessplaner-d3fac-default-rtdb.europe-west1.firebasedatabase.app")
            val uid:String = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseStorage.getInstance().reference.child("images/$uid").putFile(filePath).addOnSuccessListener {
                Toast.makeText(context, "Photo upload complete!", Toast.LENGTH_SHORT).show()

                FirebaseStorage.getInstance().reference.child("images/$uid").downloadUrl.addOnSuccessListener{uri ->
                    FirebaseAuth.getInstance().currentUser?.let {
                        database.reference.child("Users").child(it.uid)
                            .child("profileImage").setValue(uri.toString())
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}