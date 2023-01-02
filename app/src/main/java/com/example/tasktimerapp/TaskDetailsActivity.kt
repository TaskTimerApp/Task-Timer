package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class TaskDetailsActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var userData : Users
    lateinit var taskData : Tasks
    private var tasksList: ArrayList<Tasks> = arrayListOf()


    lateinit var tvTaskName : TextView
    lateinit var tvTaskDetails : TextView
    lateinit var tvTimer : Chronometer

    lateinit var btnStart : ImageButton
    lateinit var btnStop : ImageButton
    lateinit var btnReset : ImageButton

    lateinit var btnBack : ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_details)

        userData = intent.getSerializableExtra("userData") as Users
        taskData = intent.getSerializableExtra("taskData") as Tasks

        readData()

        tvTaskName = findViewById(R.id.tvTaskName)
        tvTaskDetails = findViewById(R.id.tvTaskDetails)

        tvTaskName.text = taskData.title
        tvTaskDetails.text = taskData.description


        tvTimer = findViewById(R.id.tvTimer)
        tvTimer.base = SystemClock.elapsedRealtime() - taskData.timer


        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnReset = findViewById(R.id.btnReset)
        btnBack = findViewById(R.id.btnBack)

        btnStart.setOnClickListener { start(taskData.running) }
        btnStop.setOnClickListener { stop(taskData.running) }
        btnReset.setOnClickListener { reset() }
        btnBack.setOnClickListener { homePageActivity() }

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
            tvTimer.base = SystemClock.elapsedRealtime() - taskData.timer
            tvTimer.start()
            taskData.running = true
            updateTimer()

            btnStart.isClickable = false
            btnStart.isEnabled = false
            btnStop.isClickable = true
            btnStop.isEnabled = true
            btnReset.isClickable = true
            btnReset.isEnabled = true
        }
        else {
            tvTimer.start()
            btnStart.isClickable = false
            btnStart.isEnabled = false
            btnStop.isClickable = true
            btnStop.isEnabled = true
            btnReset.isClickable = true
            btnReset.isEnabled = true
        }
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

    //////////////////////////////////////////////////
    //////////////////STOP BUTTON////////////////////
    ////////////////////////////////////////////////
    private fun stop(runningStatues : Boolean){
        if (runningStatues) {
            tvTimer.stop()
            taskData.timer = SystemClock.elapsedRealtime() - tvTimer.base
            taskData.running = false
            updateTimer()

            btnStart.isClickable = true
            btnStart.isEnabled = true
            btnStop.isClickable = false
            btnStop.isEnabled = false
        }
        else {
            btnStop.isClickable = false
            btnStop.isEnabled = false
        }
    }

    //////////////////////////////////////////////////
    //////////////////RESET BUTTON///////////////////
    ////////////////////////////////////////////////
    private fun reset(){
        tvTimer.base = SystemClock.elapsedRealtime()
        taskData.timer = 0
        taskData.running = false
        tvTimer.stop()
        updateTimer()

        btnStart.isClickable = true
        btnStart.isEnabled = true
        btnStop.isClickable = false
        btnStop.isEnabled = false
        btnReset.isClickable = false
        btnReset.isEnabled = false
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
            taskData.timer = SystemClock.elapsedRealtime() - tvTimer.base
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