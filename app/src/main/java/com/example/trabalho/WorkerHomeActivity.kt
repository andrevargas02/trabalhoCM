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

        // 1) Saudação: usa R.id.txtBemVindo conforme seu XML atual
        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { snap ->
                val nome = snap.getString("name").orEmpty()
                findViewById<TextView>(R.id.txtBemVindo)
                    .text = "Bem vindo/a\n$nome"
            }
        }

        // 2) Ajusta botões via tag
        listOf(
            R.id.btnPendingIssues,
            R.id.btnActiveIssues,
            R.id.btnHistoryWorker,
            R.id.btnMessagesWorker
        ).forEach { configHomeButton(it) }

        // 3) Navegações
        findViewById<View>(R.id.btnPendingIssues).setOnClickListener {
            startActivity(Intent(this, PendingIssuesActivity::class.java))
        }
        findViewById<View>(R.id.btnActiveIssues).setOnClickListener {
            startActivity(Intent(this, ActiveIssuesActivity::class.java))
        }
        findViewById<View>(R.id.btnHistoryWorker).setOnClickListener {
            startActivity(Intent(this, WorkerIssueHistoryActivity::class.java))
        }
        findViewById<View>(R.id.btnMessagesWorker).setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }
    }

    private fun configHomeButton(viewId: Int) {
        val root = findViewById<View>(viewId) ?: return
        val parts = root.tag?.toString()?.split("|") ?: return
        if (parts.size < 2) return

        val (label, iconTag) = parts
        val iconName = iconTag.removePrefix("@drawable/")

        root.findViewById<TextView>(R.id.txtLabel)?.text = label
        root.findViewById<ImageView>(R.id.imgIcon)
            ?.setImageResource(resources.getIdentifier(iconName, "drawable", packageName))
    }
}
