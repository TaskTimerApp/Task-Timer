package com.example.tasktimerapp

import java.io.Serializable

data class Users(
    var pk: String,
    var username: String,
    var image: String,
    var password: String? = "",
): Serializable
