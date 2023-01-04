package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tasktimerapp.databinding.ActivityHomePageBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList


class HomePageActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomePageBinding
    private val db = Firebase.firestore
    private lateinit var userData : Users
    private var tasksList: ArrayList<Tasks> = arrayListOf()

    /** Recycler View declare */
    private lateinit var rvAdapter: TasksAdapter

    var pauseOffset : Long = 0
    var totalRunning : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userData = intent.getSerializableExtra("userData") as Users
        getUserImage()


        /////////////////////////////////
        ////////READ DATA FROM DB////////
        tasksList = arrayListOf()
        readData()
        //////////////////////////////////
        //////////////////////////////////


        /////////////////////////////////
        ////////SHOW DATA ON RV////////
        rvAdapter = TasksAdapter(this)
        binding.rvTasks.adapter = rvAdapter
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        //////////////////////////////////
        //////////////////////////////////


        binding.btnAddTask.setOnClickListener { addTaskActivity() }
        binding.logoutIcon.setOnClickListener { logout() }

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
        //tasksList.clear()
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
                rvAdapter.submitList(tasksList)
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
            Log.d("ReadData", "Read: ")
        }
        Toast.makeText(this, "Deleted", Toast.LENGTH_LONG).show()
    }


    private fun getUserImage() {
        try {
            Glide.with(this)
                .load(userData.image)
                .override(600, 200)
                .into(binding.profileIV)
            binding.usernameTV.text = userData.username
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
        binding.totalTimerTV.setBase(SystemClock.elapsedRealtime() - pauseOffset)
        if (totalRunning) {
            binding.totalTimerTV.start()
        } else {
            binding.totalTimerTV.stop()
        }
    }
}