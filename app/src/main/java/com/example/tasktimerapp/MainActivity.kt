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


class MainActivity : AppCompatActivity() {

    lateinit var loginBtn : Button
    lateinit var signupBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginBtn = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener { loginPageActivity() }

        signupBtn = findViewById(R.id.signupBtn)
        signupBtn.setOnClickListener { signupPageActivity() }

    }


    //////////////////////////////////////////////////
    //////////////////Login PAGE ACTIVITY////////////
    ////////////////////////////////////////////////
    private fun loginPageActivity() {
        val intent = Intent(this, LoginPageActivity::class.java)
        startActivity(intent)
    }


    //////////////////////////////////////////////////
    //////////////////SIGNUP PAGE ACTIVITY///////////
    ////////////////////////////////////////////////
    private fun signupPageActivity() {
        val intent = Intent(this, SignUpPageActivity::class.java)
        startActivity(intent)
    }

}