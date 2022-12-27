package com.example.tasktimerapp

data class TasksFB(
    var userId: String,
    var name: String,
    var details: String,
    var timer: Long = 0,
    var running: Boolean = false
)
