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

class AddTaskActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var userData : Users

    private lateinit var taskTitleET: EditText
    private lateinit var taskDescriptionET: EditText
    private lateinit var btnAddTask: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_task)

        userData = intent.getSerializableExtra("userData") as Users

        taskTitleET = findViewById(R.id.taskTitleET)
        taskDescriptionET = findViewById(R.id.taskDescriptionET)

        btnAddTask = findViewById(R.id.btnAddTask)
        btnAddTask.setOnClickListener { saveTask() }
    }


    private fun saveTask(){
        if (taskTitleET.text.isNotEmpty() && taskDescriptionET.text.isNotEmpty()) {
            val title = taskTitleET.text.toString()
            val description = taskDescriptionET.text.toString()
            val taskObj = TasksFB(userData.pk, title, description)

            CoroutineScope(Dispatchers.IO).launch {
                //Add a new document with a generated ID
                db.collection("userTasks")
                    .add(taskObj)
                    .addOnSuccessListener { documentReference ->
                        Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                        Toast.makeText(this@AddTaskActivity, "Add task successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                    }
            }
            homePageActivity()
        }
        else {
            Toast.makeText(this, "All field are required", Toast.LENGTH_SHORT).show()
        }
    }


    private fun homePageActivity() {
        val intent = Intent(this, HomePageActivity::class.java)
        intent.putExtra("userData", userData)
        startActivity(intent)
    }
}