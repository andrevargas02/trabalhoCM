package com.example.trabalho

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val txtName = findViewById<TextView>(R.id.txtProfileName)
        val txtEmail = findViewById<TextView>(R.id.txtProfileEmail)
        val technicianStats = findViewById<LinearLayout>(R.id.technicianStats)
        val txtRating = findViewById<TextView>(R.id.txtRating)
        val txtTasksCompleted = findViewById<TextView>(R.id.txtTasksCompleted)
        val txtAverageTime = findViewById<TextView>(R.id.txtAverageTime)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get().addOnSuccessListener { doc ->
            val name = doc.getString("name").orEmpty()
            val email = doc.getString("email").orEmpty()
            val role = doc.getString("role").orEmpty()

            txtName.text = name
            txtEmail.text = email

            if (role == "technician") {
                technicianStats.visibility = View.VISIBLE
                txtRating.text = "4.5 ★★★★★" // estático, ou você pode calcular
                txtTasksCompleted.text = "Tarefas Concluídas\n${doc.getLong("issuesResolved") ?: 0}"
                txtAverageTime.text = "Tempo Médio\n${doc.getString("avgTime") ?: "25 min"}"
            } else {
                technicianStats.visibility = View.GONE
            }
        }
    }
}
