package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTaskActivity : AppCompatActivity() {

    val db = Firebase.firestore
    var userId = ""
    var userName = ""
    var img = ""

    private lateinit var taskTitleET: EditText
    private lateinit var taskDescriptionET: EditText
    private lateinit var btnAddTask: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_task)


        userId = intent.getStringExtra("userId").toString()
        userName = intent.getStringExtra("userName").toString()
        img = intent.getStringExtra("userImage").toString()

        taskTitleET = findViewById(R.id.taskTitleET)
        taskDescriptionET = findViewById(R.id.taskDescriptionET)

        btnAddTask = findViewById(R.id.btnAddTask)
        btnAddTask.setOnClickListener {
            if (taskTitleET.text.isNotEmpty() && taskDescriptionET.text.isNotEmpty()) {
                saveTask()
            }
        }
    }


    private fun saveTask(){
        val name = taskTitleET.text.toString()
        val details = taskDescriptionET.text.toString()
        val taskObj = TasksFB(userId , name , details)

        CoroutineScope(Dispatchers.IO).launch {
            //Add a new document with a generated ID
            db.collection("userTasks")
                .add(taskObj)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }
        }
        homePageActivity()
    }


    private fun homePageActivity() {
        val intent = Intent(this, HomePageActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("userImage", img)
        startActivity(intent)
    }
}