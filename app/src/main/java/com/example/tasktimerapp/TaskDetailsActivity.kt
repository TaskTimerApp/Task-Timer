package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import com.example.tasktimerapp.databinding.ActivityTaskDetailsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class TaskDetailsActivity : AppCompatActivity() {

    lateinit var binding : ActivityTaskDetailsBinding
    private val db = Firebase.firestore
    private lateinit var userData : Users
    lateinit var taskData : Tasks
    private var tasksList: ArrayList<Tasks> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userData = intent.getSerializableExtra("userData") as Users
        taskData = intent.getSerializableExtra("taskData") as Tasks
        readData()

        binding.tvTaskName.text = taskData.title
        binding.tvTaskDetails.text = taskData.description
        binding.tvTimer.base = SystemClock.elapsedRealtime() - taskData.timer

        binding.btnStart.setOnClickListener { start(taskData.running) }
        binding.btnStop.setOnClickListener { stop(taskData.running) }
        binding.btnReset.setOnClickListener { reset() }
        binding.btnBack.setOnClickListener { homePageActivity() }

        //////////////////////////////////////
        ////////CHECK TIMER CONDITIONS////////
        if (taskData.running){
            start(taskData.running)
        }
        else {
            stop(taskData.running)
        }
        ///////////////////////////////////////
        ///////////////////////////////////////


    }/** End of OnCreate */

    //////////////////////////////////////////////////
    //////////////////START BUTTON///////////////////
    ////////////////////////////////////////////////
    private fun start(runningStatus : Boolean){
        if (!runningStatus) {

            //Check if there are running timers
            checkRunningTimers(taskData.pk)
            binding.tvTimer.base = SystemClock.elapsedRealtime() - taskData.timer
            binding.tvTimer.start()
            taskData.running = true
            updateTimer()

            binding.btnStart.isClickable = false
            binding.btnStart.isEnabled = false
            binding.btnStop.isClickable = true
            binding.btnStop.isEnabled = true
            binding.btnReset.isClickable = true
            binding.btnReset.isEnabled = true
        }
        else {
            binding.tvTimer.start()
            binding.btnStart.isClickable = false
            binding.btnStart.isEnabled = false
            binding.btnStop.isClickable = true
            binding.btnStop.isEnabled = true
            binding.btnReset.isClickable = true
            binding.btnReset.isEnabled = true
        }
    }


    //////////////////////////////////////////////////
    //////////////////STOP BUTTON////////////////////
    ////////////////////////////////////////////////
    private fun stop(runningStatues : Boolean){
        if (runningStatues) {
            binding.tvTimer.stop()
            taskData.timer = SystemClock.elapsedRealtime() - binding.tvTimer.base
            taskData.running = false
            updateTimer()

            binding.btnStart.isClickable = true
            binding.btnStart.isEnabled = true
            binding.btnStop.isClickable = false
            binding.btnStop.isEnabled = false
        }
        else {
            binding.btnStop.isClickable = false
            binding.btnStop.isEnabled = false
        }
    }


    //////////////////////////////////////////////////
    //////////////////RESET BUTTON///////////////////
    ////////////////////////////////////////////////
    private fun reset(){
        binding.tvTimer.base = SystemClock.elapsedRealtime()
        binding.tvTimer.stop()
        taskData.timer = 0
        taskData.running = false
        updateTimer()

        binding.btnStart.isClickable = true
        binding.btnStart.isEnabled = true
        binding.btnStop.isClickable = false
        binding.btnStop.isEnabled = false
        binding.btnReset.isClickable = false
        binding.btnReset.isEnabled = false
    }


    //////////////////////////////////////////////////
    //////////////////READ DATA//////////////////////
    ////////////////////////////////////////////////
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
                        tasksList.add(Tasks(
                            document.id,
                            userID,
                            title,
                            description,
                            timer as Long,
                            running as Boolean
                        ))
                    }  /**  End of if condition */
                }  /**  End of for loop */
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents.", Toast.LENGTH_LONG).show()
            }
    }


    //////////////////////////////////////////////////////
    //////////////////UPDATE TASK DATA///////////////////
    ////////////////////////////////////////////////////
    private fun updateTimer(){
        CoroutineScope(Dispatchers.IO).launch {
            val updatedTask = TasksFB(taskData.userId, taskData.title, taskData.description, taskData.timer, taskData.running)
            db.collection("userTasks").document(taskData.pk)
                .set(updatedTask)
        }
    }


    /////////////////////////////////////////////////////
    //////////////////BACK TO HOMEPAGE///////////////////
    ////////////////////////////////////////////////////
    private fun homePageActivity() {
        if (taskData.running) {
            taskData.timer = SystemClock.elapsedRealtime() - binding.tvTimer.base
            updateTimer()
        }
        val intent = Intent(this, HomePageActivity::class.java)
        intent.putExtra("userData", userData)
        startActivity(intent)
    }


    /////////////////////////////////////////////////////
    //////////////////CHECK TIMER STATE//////////////////
    ////////////////////////////////////////////////////
    private fun checkRunningTimers(taskPK : String){
        tasksList.clear()
        db.collection("userTasks")
            .get()
            .addOnSuccessListener { tasksResult ->
                for (document in tasksResult) {
                    val userID = document.data.get("userId").toString()
                    val title = document.data.get("title").toString()
                    val description = document.data.get("description").toString()
                    val timer = document.data.get("timer")
                    var running = document.data.get("running")

                    if (userID == userData.pk) {
                        if (running == true && document.id != taskPK) {
                            running = false
                            tasksList.add(
                                Tasks(
                                    document.id,
                                    userID,
                                    title,
                                    description,
                                    timer as Long,
                                    running
                                )
                            )
                        }
                    }
                }

                //Update running status
                if (tasksList.isNotEmpty()) {
                    for (i in 0..tasksList.size - 1) {
                        updateRunningStatus(tasksList[i])
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents.", Toast.LENGTH_LONG).show()
            }
    }


    /////////////////////////////////////////////////////
    //////////////////SET RUNNING STATUS TO FALSE////////
    ////////////////////////////////////////////////////
    private fun updateRunningStatus(taskList: Tasks){
        if (taskList != null) {
            val updatedTask = TasksFB(
                taskList.userId,
                taskList.title,
                taskList.description,
                taskList.timer,
                taskList.running
            )
            db.collection("userTasks").document(taskList.pk)
                .set(updatedTask)
        } else {
            Toast.makeText(this, "There is no timer running", Toast.LENGTH_LONG).show()
        }

    }

}