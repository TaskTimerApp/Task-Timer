package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class LoginPageActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var userData : Users
    private var usersList: ArrayList<Users> = arrayListOf()

    lateinit var userNameEtLogin : EditText
    lateinit var passwordEtLogin : EditText
    lateinit var loginBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)


        userData = Users("","","", "")

        //Login variable
        userNameEtLogin = findViewById(R.id.userNameEtLogin)
        passwordEtLogin = findViewById(R.id.passwordEtLogin)
        loginBtn = findViewById(R.id.loginBtn)

        loginBtn.setOnClickListener { loginUser() }
    }

    //////////////////////////////////////////////////
    //////////////////LOGIN//////////////////////////
    ////////////////////////////////////////////////
    private fun loginUser(){
        if (userNameEtLogin.text.isNotEmpty() && passwordEtLogin.text.isNotEmpty()){
            usersList.clear()
            CoroutineScope(Dispatchers.IO).launch {
                db.collection("users")
                    .get()
                    .addOnSuccessListener { usersResult ->
                        for (document in usersResult) {
                            val username = document.data.get("username").toString()
                            val password = document.data.get("password").toString()
                            val image = document.data.get("image").toString()
                            usersList.add(Users(document.id, username, image, password))
                        }
                        checkUser()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this@LoginPageActivity, "Error getting documents.", Toast.LENGTH_LONG).show()
                    }
            }

        } else {
            Toast.makeText(this, "All field are required", Toast.LENGTH_SHORT).show()
        }
    }

    //////////////////////////////////////////////////
    //////////////////CHECK USER//////////////////////
    ////////////////////////////////////////////////
    private fun checkUser(){
        val usernameET = userNameEtLogin.text.toString()
        val passwordET = passwordEtLogin.text.toString()
        var exist = false

        for (i in 0 .. usersList.size -1) {
            if (usernameET == usersList[i].username) {
                if (passwordET == usersList[i].password) {
                    userData = Users(usersList[i].pk, usersList[i].username, usersList[i].image)
                    exist = true
                }
            }
        }
        if (exist){
            Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show()
            homePageActivity()
        }else{
            Toast.makeText(this, "username or password is not correct", Toast.LENGTH_LONG).show()
        }
    }


    //////////////////////////////////////////////////
    //////////////////HOME PAGE ACTIVITY/////////////
    ////////////////////////////////////////////////
    private fun homePageActivity() {
        val intent = Intent(this, HomePageActivity::class.java)
        intent.putExtra("userData", userData)
        startActivity(intent)
    }
}