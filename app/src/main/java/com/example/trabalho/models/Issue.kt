package com.example.trabalho.models
import java.io.Serializable

data class Issue(
    val description: String = "",
    val urgency: String = "",
    val status: String = "",
    val image_path: String = "",
    val createdAt: Long = 0,
    val resolvedAt: Long = 0,
    val createdBy: String = "" ,
    val technicianId: String = "",
    val location_id: String = ""
) : Serializable
