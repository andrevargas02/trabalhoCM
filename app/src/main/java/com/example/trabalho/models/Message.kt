package com.example.trabalho.models

import com.google.firebase.Timestamp

data class Message(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Timestamp? = null
)
