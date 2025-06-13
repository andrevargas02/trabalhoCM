package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Issue
import com.google.firebase.firestore.FirebaseFirestore

class ManageIssuesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ManageIssueAdapter
    private val issues = mutableListOf<Pair<String, Issue>>() // ID + dados
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_issues)

        // 1) Back button → volta ao menu de Admin
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        // 2) Configura RecyclerView e Adapter
        recyclerView = findViewById(R.id.recyclerViewIssues)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ManageIssueAdapter(issues) { docId, issue ->
            val intent = Intent(this, EditIssueActivity::class.java).apply {
                putExtra("docId", docId)
                putExtra("issue", issue)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // 3) Carrega os dados
        loadIssues()
    }

    private fun loadIssues() {
        db.collection("issues")
            .get()
            .addOnSuccessListener { result ->
                issues.clear()
                for (doc in result) {
                    val issue = doc.toObject(Issue::class.java)
                    issues.add(doc.id to issue)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // opcional: Toast ou log
            }
    }
}
