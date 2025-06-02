package com.example.trabalho

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Issue
import com.google.firebase.firestore.FirebaseFirestore

class ActiveIssuesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IssueAdapter
    private val activeIssuesList = mutableListOf<Issue>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_issues)

        // 1) Configura o RecyclerView com um LinearLayoutManager e o nosso IssueAdapter
        recyclerView = findViewById(R.id.recyclerViewActiveIssues)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Note que aqui passamos uma MutableList para o adapter,
        // para podermos dar clear() e add() depois.
        adapter = IssueAdapter(activeIssuesList)
        recyclerView.adapter = adapter

        // 2) Faz query no Firestore para buscar apenas avarias cujo campo status == "ativa"
        db.collection("issues")
            .whereEqualTo("status", "ativa")
            .get()
            .addOnSuccessListener { result ->
                // Limpa a lista local e preenche apenas com aqueles documentos em que status="ativa"
                activeIssuesList.clear()
                for (doc in result) {
                    val issue = doc.toObject(Issue::class.java)
                    activeIssuesList.add(issue)
                }
                // Notifica o adapter que os dados mudaram
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Erro ao buscar avarias ativas: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
