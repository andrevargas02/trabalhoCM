package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WorkerHomeActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db   by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_home)

        // 1) Preenche o nome do trabalhador no TextView de boas-vindas
        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { snap ->
                val nome = snap.getString("name").orEmpty()
                findViewById<TextView>(R.id.txtBemVindo)?.text = "Bem vindo/a\n$nome"
            }
        }

        // 2) Configura cada botão para ajustar texto e ícone via tag
        listOf(
            R.id.btnPendingIssues,
            R.id.btnActiveIssues,
            R.id.btnHistoryWorker,
            R.id.btnMessagesWorker
        ).forEach { configHomeButton(it) }

        // 3) Navegações:

        // “Avarias Pendentes” → abre PendingIssuesActivity
        findViewById<View>(R.id.btnPendingIssues).setOnClickListener {
            startActivity(Intent(this, PendingIssuesActivity::class.java))
        }

        // “Avarias Ativas” → abre ActiveIssuesActivity
        findViewById<View>(R.id.btnActiveIssues).setOnClickListener {
            startActivity(Intent(this, ActiveIssuesActivity::class.java))
        }

        // “Histórico de avarias” → abre WorkerIssueHistoryActivity
        findViewById<View>(R.id.btnHistoryWorker).setOnClickListener {
            startActivity(Intent(this, WorkerIssueHistoryActivity::class.java))
        }

        // “Lista de mensagens” → a implementar
        findViewById<View>(R.id.btnMessagesWorker).setOnClickListener {
            // startActivity(Intent(this, MessagesActivity::class.java))
        }
    }

    /**
     * Lê a tag do include (formato "Texto|@drawable/icone") e aplica nos views
     * txtLabel e imgIcon que existem dentro de item_home_button.xml.
     */
    private fun configHomeButton(viewId: Int) {
        val root = findViewById<View>(viewId) ?: return

        val tagParts = root.tag?.toString()?.split("|") ?: return
        if (tagParts.size < 2) return

        val (label, drawablePath) = tagParts
        val drawableName = drawablePath.removePrefix("@drawable/")

        val txtLabel = root.findViewById<TextView>(R.id.txtLabel)
        val imgIcon  = root.findViewById<ImageView>(R.id.imgIcon)

        txtLabel?.text = label
        imgIcon?.setImageResource(
            resources.getIdentifier(drawableName, "drawable", packageName)
        )
    }
}
