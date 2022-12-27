package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
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

    val db = Firebase.firestore
    lateinit var taskData : Tasks
    private var tasksList: ArrayList<Tasks> = arrayListOf()

    var userName = ""
    var img = ""

    var taskPK = ""
    var userId = ""
    var taskName = ""
    var taskDetails = ""
    var taskTimer : Long = 0
    var timerRunning = false

    lateinit var tvTaskName : TextView
    lateinit var tvTaskDetails : TextView
    lateinit var tvTimer : Chronometer

    lateinit var btnStart : ImageButton
    lateinit var btnStop : ImageButton
    lateinit var btnReset : ImageButton

    lateinit var btnBack : ImageButton

    var pauseOffset : Long = 0
    var running : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_details)


        userName = intent.getStringExtra("userName").toString()
        img = intent.getStringExtra("userImage").toString()

        taskPK = intent.getStringExtra("taskPK").toString()
        userId = intent.getStringExtra("userId").toString()
        taskName = intent.getStringExtra("taskName").toString()
        taskDetails = intent.getStringExtra("taskDetails").toString()
        taskTimer = intent.getLongExtra("timer", 0)
        timerRunning = intent.getBooleanExtra("running", false)

        taskData = Tasks(taskPK, userId, taskName, taskDetails, taskTimer, timerRunning)
        readData()

        pauseOffset = taskData.timer
        running = taskData.running


        tvTaskName = findViewById(R.id.tvTaskName)
        tvTaskDetails = findViewById(R.id.tvTaskDetails)

        tvTaskName.text = taskName
        tvTaskDetails.text = taskDetails


        tvTimer = findViewById(R.id.tvTimer)
        tvTimer.base = SystemClock.elapsedRealtime() - pauseOffset


        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnReset = findViewById(R.id.btnReset)
        btnBack = findViewById(R.id.btnBack)

        btnStart.setOnClickListener { start(running) }
        btnStop.setOnClickListener { stop(running) }
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

            tvTimer.base = SystemClock.elapsedRealtime() - pauseOffset
            tvTimer.start()
            running = true

            taskData.running = running
            taskData.timer = pauseOffset
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
                    val name = document.data.get("name").toString()
                    val details = document.data.get("details").toString()
                    val timer = document.data.get("timer")
                    val running = document.data.get("running")

                    if (userID == userId) {
                        tasksList.add(Tasks(
                            document.id,
                            userID,
                            name,
                            details,
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
            pauseOffset = SystemClock.elapsedRealtime() - tvTimer.base
            running = false

            taskData.running = running
            taskData.timer = pauseOffset
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
        pauseOffset = 0
        running = false
        tvTimer.stop()

        taskData.running = running
        taskData.timer = pauseOffset
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
            var updatedTask = TasksFB(taskData.userId, taskData.name, taskData.details, taskData.timer, taskData.running)
            db.collection("userTasks").document(taskData.pk)
                .set(updatedTask)
        }
    }


    /////////////////////////////////////////////////////
    //////////////////BACK TO HOMEPAGE///////////////////
    ////////////////////////////////////////////////////
    private fun homePageActivity() {
        if (taskData.running) {
            pauseOffset = SystemClock.elapsedRealtime() - tvTimer.base
            taskData.timer = pauseOffset
            updateTimer()
        }
        val intent = Intent(this, HomePageActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("userImage", img)
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
                    val name = document.data.get("name").toString()
                    val details = document.data.get("details").toString()
                    val timer = document.data.get("timer")
                    var running = document.data.get("running")

                    if (userID == userId) {
                        if (running == true && document.id != taskPK) {
                            running = false
                            tasksList.add(
                                Tasks(
                                    document.id,
                                    userID,
                                    name,
                                    details,
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
                taskList.name,
                taskList.details,
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