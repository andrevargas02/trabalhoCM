package com.example.trabalho.models

data class StatusUpdate(
    val statusID: String = "",
    val status: String = "",
    val timestamp: Long = 0L,
    val issueID: String = "",
    val updatedBy: String = ""
)
