package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class SignUpPageActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    lateinit var userNameEtSignup : EditText
    lateinit var userPasswordEtSignup : EditText
    lateinit var imageURLEt : EditText
    lateinit var signupBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_page)

        userNameEtSignup = findViewById(R.id.userNameEtSignup)
        userPasswordEtSignup = findViewById(R.id.userPasswordEtSignup)
        imageURLEt = findViewById(R.id.imageURLEt)
        signupBtn = findViewById(R.id.signupBtn)

        signupBtn.setOnClickListener { signupUser() }

    }

    //////////////////////////////////////////////////
    /////////////////SIGNUP//////////////////////
    ////////////////////////////////////////////////
    private fun signupUser(){
        if (userNameEtSignup.text.isNotEmpty() && userPasswordEtSignup.text.isNotEmpty() && imageURLEt.text.isNotEmpty()) {
            val username = userNameEtSignup.text.toString()
            val password = userPasswordEtSignup.text.toString()
            val image = imageURLEt.text.toString()

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