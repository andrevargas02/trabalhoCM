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
    private lateinit var adapter: IssueAdapter
    private val activeIssuesList = mutableListOf<Issue>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_issues)

        // 1) Botão de voltar → volta para a home do trabalhador
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, WorkerHomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        // 2) Configura o RecyclerView
        recyclerView = findViewById(R.id.recyclerViewActiveIssues)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = IssueAdapter(activeIssuesList)
        recyclerView.adapter = adapter

        // 3) Obtém o utilizador atual
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 4) Lê o papel (role) do utilizador e faz a query adequada
        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role")
                if (role == null) {
                    Toast.makeText(
                        this,
                        "Erro ao obter o tipo de utilizador",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                val statuses = listOf("ativa", "em progresso")
                val query = when (role) {
                    "client" -> db.collection("issues")
                        .whereIn("status", statuses)
                        .whereEqualTo("createdBy", currentUser.uid)

                    "trabalhador" -> db.collection("issues")
                        .whereIn("status", statuses)
                        .whereEqualTo("technicianId", currentUser.uid)

                    else -> {
                        Toast.makeText(
                            this,
                            "Permissão negada: papel desconhecido",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addOnSuccessListener
                    }
                }

                // 5) Executa a query e atualiza a lista
                query.get()
                    .addOnSuccessListener { result ->
                        activeIssuesList.clear()
                        for (docIssue in result) {
                            val issue = docIssue.toObject(Issue::class.java)
                            activeIssuesList.add(issue)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Erro ao buscar avarias: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Erro ao carregar dados do utilizador",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
