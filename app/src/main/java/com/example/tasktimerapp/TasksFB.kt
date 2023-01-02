package com.example.tasktimerapp

data class TasksFB(
    var userId: String,
    var title: String,
    var description: String,
    var timer: Long = 0,
    var running: Boolean = false
)
