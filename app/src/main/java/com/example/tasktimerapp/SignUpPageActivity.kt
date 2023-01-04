package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.tasktimerapp.databinding.ActivitySignupPageBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class SignUpPageActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignupPageBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupBtn.setOnClickListener { signupUser() }

    }

    //////////////////////////////////////////////////
    /////////////////SIGNUP//////////////////////
    ////////////////////////////////////////////////
    private fun signupUser(){
        if (binding.userNameEtSignup.text.isNotEmpty() && binding.userPasswordEtSignup.text.isNotEmpty() && binding.imageURLEt.text.isNotEmpty()) {
            val username = binding.userNameEtSignup.text.toString()
            val password = binding.userPasswordEtSignup.text.toString()
            val image = binding.imageURLEt.text.toString()

            //check if the username is exist
            val isExist = checkUsername(username)
            if (isExist) {
                Toast.makeText(this, "This username already exists!!", Toast.LENGTH_SHORT).show()
            }
            else {
                val userObj = UsersFB(username, password, image)
                CoroutineScope(Dispatchers.IO).launch {
                    //Add a new document with a generated ID
                    db.collection("users")
                        .add(userObj)
                        .addOnSuccessListener {
                            Toast.makeText(this@SignUpPageActivity, "Sign up successfully", Toast.LENGTH_SHORT).show()
                            loginPageActivity()
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error adding document", e)
                        }
                }
            }

        } else {
            Toast.makeText(this, "All field are required", Toast.LENGTH_SHORT).show()
        }
    }


    //////////////////////////////////////////////////
    //////////////CHECK USERNAME EXIST///////////////
    ////////////////////////////////////////////////
    private fun checkUsername(username: String):Boolean{
        var exist = false
        db.collection("users")
            .get()
            .addOnSuccessListener { usersResult ->
                for (document in usersResult) {
                    val getUsername = document.data.get("username").toString()
                    if (username == getUsername) {
                        exist = true
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@SignUpPageActivity, "Error getting documents.", Toast.LENGTH_LONG).show()
            }
        return exist

    }


    //////////////////////////////////////////////////
    //////////////////HOME PAGE ACTIVITY/////////////
    ////////////////////////////////////////////////
    private fun loginPageActivity() {
        val intent = Intent(this, LoginPageActivity::class.java)
        startActivity(intent)
    }
}