package com.example.tasktimerapp

import java.io.Serializable

data class Tasks(
    var pk: String,
    var userId: String,
    var title: String,
    var description: String,
    var timer: Long = 0,
    var running: Boolean = false
): Serializable
