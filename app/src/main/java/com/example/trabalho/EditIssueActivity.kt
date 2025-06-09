package com.example.trabalho


import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalho.models.Issue
import com.example.trabalho.models.StatusUpdate
import com.example.trabalho.models.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditIssueActivity : AppCompatActivity() {

    private lateinit var issueId: String
    private lateinit var issue: Issue
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_issue)

        // Receber dados
        issueId = intent.getStringExtra("docId")!!
        issue = intent.getSerializableExtra("issue") as Issue

        // Preencher campos
        findViewById<EditText>(R.id.inputDescription).setText(issue.description)
        findViewById<EditText>(R.id.inputLocation).setText(issue.location_id)

        val spinner = findViewById<Spinner>(R.id.spinnerUrgency)
        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Baixa", "Média", "Alta")
        )
        spinner.setSelection(listOf("Baixa", "Média", "Alta").indexOf(issue.urgency))

        // Ocultar botão de imagem
        findViewById<Button>(R.id.btnAttach).visibility = Button.GONE

        // Submeter alterações
        val btnSubmit = findViewById<Button>(R.id.btnSubmitIssue)
        btnSubmit.text = "Atualizar Avaria"
        btnSubmit.setOnClickListener {
            val updatedIssue = issue.copy(
                description = findViewById<EditText>(R.id.inputDescription).text.toString(),
                urgency = spinner.selectedItem.toString(),
                location_id = findViewById<EditText>(R.id.inputLocation).text.toString()
            )
            showEditDialog(updatedIssue)
        }
    }

    private fun showEditDialog(updatedIssue: Issue) {
        val statuses = arrayOf("ativa", "em progresso", "resolvida", "cancelada")
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        val statusSpinner = Spinner(this)
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)
        statusSpinner.setSelection(statuses.indexOf(updatedIssue.status))

        val techSpinner = Spinner(this)
        val technicianNames = mutableListOf<String>()
        val technicianIds = mutableListOf<String>()

        // Carregar técnicos do Firestore
        db.collection("users")
            .whereEqualTo("role", "trabalhador")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    technicianNames.add(doc.getString("name") ?: "Desconhecido")
                    technicianIds.add(doc.id)
                }

                techSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, technicianNames)

                layout.addView(TextView(this).apply { text = "Novo estado" })
                layout.addView(statusSpinner)
                layout.addView(TextView(this).apply { text = "Selecionar técnico" })
                layout.addView(techSpinner)

                AlertDialog.Builder(this)
                    .setTitle("Atualizar Estado e Técnico")
                    .setView(layout)
                    .setPositiveButton("Salvar") { _, _ ->
                        val newStatus = statusSpinner.selectedItem.toString()
                        val technicianID = technicianIds[techSpinner.selectedItemPosition]
                        val finalIssue = updatedIssue.copy(
                            status = newStatus,
                            technicianId = technicianID,
                            createdBy = issue.createdBy
                        )

                        db.collection("issues").document(issueId).set(finalIssue)
                            .addOnSuccessListener {
                                // 🔁 Guardar StatusUpdate
                                val statusUpdate = StatusUpdate(
                                    statusID = db.collection("status_updates").document().id,
                                    status = newStatus,
                                    timestamp = System.currentTimeMillis(),
                                    issueID = issueId,
                                    updatedBy = auth.currentUser?.uid ?: "desconhecido"
                                )
                                db.collection("status_updates")
                                    .document(statusUpdate.statusID)
                                    .set(statusUpdate)

                                // 🔔 Guardar Notification
                                val notification = Notification(
                                    notificationID = db.collection("notifications").document().id,
                                    message = "O estado da sua avaria foi atualizado para \"$newStatus\".",
                                    timestamp = System.currentTimeMillis(),
                                    userID = issue.createdBy,
                                    issueID = issueId
                                )
                                db.collection("notifications")
                                    .document(notification.notificationID)
                                    .set(notification)

                                Toast.makeText(this, "Avaria atualizada com sucesso", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao atualizar avaria", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar técnicos", Toast.LENGTH_SHORT).show()
            }
    }
}