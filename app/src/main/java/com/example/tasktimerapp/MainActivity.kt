package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tasktimerapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener { loginPageActivity() }
        binding.signupBtn.setOnClickListener { signupPageActivity() }
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