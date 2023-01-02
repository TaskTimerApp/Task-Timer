package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class HomePageActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var userData : Users
    private var tasksList: ArrayList<Tasks> = arrayListOf()

    /** Recycler View declare */
    private lateinit var rvTasks: RecyclerView
    private lateinit var rvAdapter: TasksAdapter

    lateinit var btnAddTask : ImageButton
    lateinit var logoutIcon : ImageButton

    lateinit var profileIV : ImageView
    lateinit var usernameTV : TextView

    lateinit var totalTimerTV : Chronometer
    var pauseOffset : Long = 0
    var totalRunning : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        userData = intent.getSerializableExtra("userData") as Users

        profileIV = findViewById(R.id.profileIV)
        usernameTV = findViewById(R.id.usernameTV)
        getUserImage()


        totalTimerTV = findViewById(R.id.totalTimerTV)

        rvTasks = findViewById(R.id.rvTasks)

        /////////////////////////////////
        ////////READ DATA FROM DB////////
        tasksList = arrayListOf()
        readData()
        //////////////////////////////////
        //////////////////////////////////

        /////////////////////////////////
        ////////SHOW DATA ON RV////////
        rvAdapter = TasksAdapter(this, tasksList)
        rvTasks.adapter = rvAdapter
        rvTasks.layoutManager = LinearLayoutManager(this)
        //////////////////////////////////
        //////////////////////////////////

        btnAddTask = findViewById(R.id.btnAddTask)
        btnAddTask.setOnClickListener { addTaskActivity() }

        logoutIcon = findViewById(R.id.logoutIcon)
        logoutIcon.setOnClickListener { logout() }

    }


    private fun addTaskActivity() {
        val intent = Intent(this, AddTaskActivity::class.java)
        intent.putExtra("userData", userData)
        startActivity(intent)
    }


    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        Toast.makeText(this, "Logout successfully", Toast.LENGTH_LONG).show()
        startActivity(intent)
    }


    fun taskDetailsActivity(taskData: Tasks) {
        val intent = Intent(this, TaskDetailsActivity::class.java)
        intent.putExtra("userData", userData)
        intent.putExtra("taskData", taskData)
        startActivity(intent)
    }


    private fun readData(){
        tasksList.clear()
        db.collection("userTasks")
            .get()
            .addOnSuccessListener { tasksResult ->
                for (document in tasksResult) {
                    val userID = document.data.get("userId").toString()
                    val title = document.data.get("title").toString()
                    val description = document.data.get("description").toString()
                    val timer = document.data.get("timer")
                    val running = document.data.get("running")
                    if (userID == userData.pk) {
                        tasksList.add(
                            Tasks(
                                document.id,
                                userID,
                                title,
                                description,
                                timer as Long,
                                running as Boolean
                            )
                        )
                    }
                }
                rvAdapter.rvUpdate(tasksList)
                calcTotalTimer()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents.", Toast.LENGTH_LONG).show()
            }
    }


    fun deleteTask(taskData: Tasks){
        //Asking user if approve delete the task "alertDialog"
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("userTasks").document(taskData.pk).delete()
            pauseOffset = 0
            readData()
        }
        Toast.makeText(this, "Deleted", Toast.LENGTH_LONG).show()
    }


    private fun getUserImage() {
        try {
            Glide.with(this)
                .load(userData.image)
                .override(600, 200)
                .into(profileIV)
            usernameTV.text = userData.username
        } catch (e:Exception){
            Log.d("Catch", "No image: $e")
        }
    }


    private fun calcTotalTimer(){
        for (i in 0 .. tasksList.size -1){
            pauseOffset += tasksList[i].timer
            if (tasksList[i].running){
                totalRunning = true
            }
        }
        totalTimerTV.setBase(SystemClock.elapsedRealtime() - pauseOffset)
        if (totalRunning) {
            totalTimerTV.start()
        } else {
            totalTimerTV.stop()
        }
    }
}