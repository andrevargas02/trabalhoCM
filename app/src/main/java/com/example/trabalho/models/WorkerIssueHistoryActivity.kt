package com.example.trabalho

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Issue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WorkerIssueHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IssueAdapter
    private val issueHistoryList = mutableListOf<Issue>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_issue_history)

        // 1) Configura RecyclerView + Adapter
        recyclerView = findViewById(R.id.recyclerViewIssueHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = IssueAdapter(issueHistoryList)
        recyclerView.adapter = adapter

        // 2) Carrega todas as avarias onde technicianId == UID do usuário logado
        loadIssueHistory()
    }

    private fun loadIssueHistory() {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("issues")
            .whereEqualTo("technicianId", uid)
            .get()
            .addOnSuccessListener { result ->
                issueHistoryList.clear()
                for (doc in result) {
                    val issue = doc.toObject(Issue::class.java)
                    issueHistoryList.add(issue)
                }
                adapter.notifyDataSetChanged()
                Toast.makeText(
                    this,
                    "Foram encontradas ${issueHistoryList.size} avarias.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Erro ao carregar histórico: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
