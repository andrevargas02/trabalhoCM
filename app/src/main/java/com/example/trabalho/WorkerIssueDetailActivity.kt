package com.example.trabalho

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalho.models.Issue
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class WorkerIssueDetailActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_issue_detail)

        val inputDescription = findViewById<EditText>(R.id.inputDescription)
        val inputLocation    = findViewById<EditText>(R.id.inputLocation)
        val spinnerUrgency   = findViewById<Spinner>(R.id.spinnerUrgency)
        val btnSubmit        = findViewById<Button>(R.id.btnSubmitIssue)

        // 1) configura o spinner
        val urgencies = listOf("Baixa", "Média", "Alta")
        spinnerUrgency.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            urgencies
        )

        // 2) lê o docId do Intent
        val docId = intent.getStringExtra("docId")
        if (docId.isNullOrBlank()) {
            Toast.makeText(this, "ID da avaria em falta", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 3) carrega os dados atuais (DocumentSnapshot)
        db.collection("issues").document(docId).get()
            .addOnSuccessListener { snap: DocumentSnapshot ->
                val issue = snap.toObject(Issue::class.java)
                if (issue == null) {
                    Toast.makeText(this, "Avaria não encontrada", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }
                inputDescription.setText(issue.description)
                inputLocation.setText(issue.location_id)
                val idx = urgencies.indexOf(issue.urgency)
                if (idx >= 0) spinnerUrgency.setSelection(idx)
            }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(this, "Erro ao carregar avaria: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }

        // 4) ao clicar em “Atualizar Avaria”, grava no Firestore
        btnSubmit.setOnClickListener {
            val newDesc = inputDescription.text.toString().trim()
            val newLoc  = inputLocation.text.toString().trim()
            val newUrg  = spinnerUrgency.selectedItem as String

            if (newDesc.isBlank() || newLoc.isBlank()) {
                Toast.makeText(this, "Preencha descrição e localização", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("issues").document(docId)
                .update(
                    mapOf(
                        "description"  to newDesc,
                        "location_id"  to newLoc,
                        "urgency"      to newUrg
                    )
                )
                .addOnSuccessListener { _: Void? ->
                    Toast.makeText(this, "Avaria atualizada!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e: Exception ->
                    Toast.makeText(this, "Falha na atualização: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
