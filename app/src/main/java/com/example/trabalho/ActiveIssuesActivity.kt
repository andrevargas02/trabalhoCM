package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Issue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActiveIssuesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ManageIssueAdapter
    private val activeIssuesList = mutableListOf<Pair<String, Issue>>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_issues)

        // 1) Botão de voltar → WorkerHome
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            Intent(this, WorkerHomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }.also {
                startActivity(it)
                finish()
            }
        }

        // 2) Recycler + Adapter (reutiliza ManageIssueAdapter do admin)
        recyclerView = findViewById(R.id.recyclerViewActiveIssues)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ManageIssueAdapter(activeIssuesList) { docId, issue ->
            // abre EXACTAMENTE o EditIssueActivity (mesmo XML do admin)
            Intent(this, EditIssueActivity::class.java).apply {
                putExtra("docId", docId)
                putExtra("issue", issue)
            }.also { startActivity(it) }
        }
        recyclerView.adapter = adapter

        // 3) Só avarias do trabalhador
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 4) Query: status = ativa|em progresso  && technicianId = currentUser.uid
        val statuses = listOf("ativa", "em progresso")
        db.collection("issues")
            .whereIn("status", statuses)
            .whereEqualTo("technicianId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                activeIssuesList.clear()
                for (doc in result) {
                    val issue = doc.toObject(Issue::class.java)
                    activeIssuesList.add(doc.id to issue)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao buscar avarias: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
