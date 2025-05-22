package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnIssues = findViewById<Button>(R.id.btnIssues)
        val btnCreateIssue = findViewById<Button>(R.id.btnCreateIssue)
        val btnMessages = findViewById<Button>(R.id.btnMessages)

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        btnIssues.setOnClickListener {
            val intent = Intent(this, IssueListActivity::class.java)
            startActivity(intent)
        }
        btnCreateIssue.setOnClickListener {
            val intent = Intent(this, CreateIssueActivity::class.java)
            startActivity(intent)
        }
        btnMessages.setOnClickListener {
            // TODO: Start MessagesActivity
        }

        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get().addOnSuccessListener { doc ->
            val role = doc.getString("role") ?: ""
            if (role == "gestor") {
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}