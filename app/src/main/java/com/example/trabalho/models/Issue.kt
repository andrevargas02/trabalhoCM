package com.example.trabalho.models

data class Issue(
    val description: String = "",
    val urgency: String = "",
    val status: String = "",
    val image_path: String = "",
    val createdAt: String = "",
    val createdBy: String = "",
    val technicianId: String = "",
    val location_id: String = ""
)
