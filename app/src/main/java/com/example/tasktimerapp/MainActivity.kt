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
    val db = Firebase.firestore
    var userId = ""
    var userName = ""
    var img = ""
    private var usersList: ArrayList<Users> = arrayListOf()

    lateinit var userNameEtSignup : EditText
    lateinit var userPasswordEtSignup : EditText
    lateinit var imageURLEt : EditText
    lateinit var signupBtn : Button

    lateinit var userNameEtLogin : EditText
    lateinit var passwordEtLogin : EditText
    lateinit var loginBtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sign up variables
        userNameEtSignup = findViewById(R.id.userNameEtSignup)
        userPasswordEtSignup = findViewById(R.id.userPasswordEtSignup)
        imageURLEt = findViewById(R.id.imageURLEt)
        signupBtn = findViewById(R.id.signupBtn)

        signupBtn.setOnClickListener {
            if (userNameEtSignup.text.isNotEmpty() && userPasswordEtSignup.text.isNotEmpty() && imageURLEt.text.isNotEmpty()) {
                signupUser()
            } else {
                Toast.makeText(this, "All field are required", Toast.LENGTH_SHORT).show()
            }
        }

        //Login variable
        userNameEtLogin = findViewById(R.id.userNameEtLogin)
        passwordEtLogin = findViewById(R.id.passwordEtLogin)
        loginBtn = findViewById(R.id.loginBtn)

        loginBtn.setOnClickListener {
            if (userNameEtLogin.text.isNotEmpty() && passwordEtLogin.text.isNotEmpty()){
                loginUser()
            }else {
                Toast.makeText(this, "All field are required", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //////////////////////////////////////////////////
    /////////////////SIGNUP//////////////////////
    ////////////////////////////////////////////////
    private fun signupUser(){
        val username = userNameEtSignup.text.toString()
        val password = userPasswordEtSignup.text.toString()
        val image = imageURLEt.text.toString()
        val userObj = UsersFB(username, password, image)

        CoroutineScope(Dispatchers.IO).launch {
            //Add a new document with a generated ID
            db.collection("users")
                .add(userObj)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }
        }
        Toast.makeText(this, "Sign up successfully, please login", Toast.LENGTH_SHORT).show()
    }


    //////////////////////////////////////////////////
    //////////////////LOGIN//////////////////////////
    ////////////////////////////////////////////////
    private fun loginUser(){
        usersList.clear()
        db.collection("users")
            .get()
            .addOnSuccessListener { usersResult ->
                for (document in usersResult) {
                    val username = document.data.get("username").toString()
                    val password = document.data.get("password").toString()
                    val image = document.data.get("image").toString()
                    usersList.add(Users(document.id, username, password, image))
                }
                checkUser()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents.", Toast.LENGTH_LONG).show()
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
                    userId = usersList[i].pk
                    userName = usersList[i].username
                    img = usersList[i].image
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
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("userImage", img)
        startActivity(intent)
    }

}