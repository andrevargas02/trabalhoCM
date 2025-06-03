package com.example.trabalho

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Issue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PendingIssuesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PendingIssueAdapter
    private val pendingList = mutableListOf<Pair<String, Issue>>() // (docId, Issue)
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_issues)

        // 1) Configura RecyclerView + Adapter
        recyclerView = findViewById(R.id.recyclerViewPendingIssues)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PendingIssueAdapter(pendingList) { docId, _ ->
            acceptIssue(docId)
        }
        recyclerView.adapter = adapter

        // 2) Carrega as avarias pendentes (technicianId == "")
        loadPendingIssues()
    }

    private fun loadPendingIssues() {
        db.collection("issues")
            .whereEqualTo("technicianId", "")
            .get()
            .addOnSuccessListener { result ->
                pendingList.clear()
                for (doc in result) {
                    val issue = doc.toObject(Issue::class.java)
                    pendingList.add(Pair(doc.id, issue))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Erro ao buscar avarias pendentes: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun acceptIssue(docId: String) {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("issues").document(docId)
            .update(
                mapOf(
                    "technicianId" to uid,
                    "status" to "em progresso"
                )
            )
            .addOnSuccessListener {
                Toast.makeText(this, "Avaria aceite com sucesso!", Toast.LENGTH_SHORT).show()
                loadPendingIssues()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Falha ao aceitar avaria: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
