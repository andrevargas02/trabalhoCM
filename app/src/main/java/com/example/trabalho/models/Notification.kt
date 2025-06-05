package com.example.trabalho.models

data class Notification(
    val notificationID: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val userID: String = "",
    val issueID: String = ""
)
