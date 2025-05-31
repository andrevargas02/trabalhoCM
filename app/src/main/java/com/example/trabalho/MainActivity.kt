package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    /* ---------- Firebase ---------- */
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db   by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* ---------- 1.   CONFIGURAR ÍCONE + TEXTO DE CADA BOTÃO ---------- */
        listOf(
            R.id.btnCreateIssue,
            R.id.btnIssues,
            R.id.btnHistory,
            R.id.btnMessages
        ).forEach { configHomeButton(it) }

        /* ---------- 2.   NAVEGAÇÃO ---------- */
        findViewById<View>(R.id.btnCreateIssue).setOnClickListener {
            startActivity(Intent(this, CreateIssueActivity::class.java))
        }
        findViewById<View>(R.id.btnIssues).setOnClickListener {
            startActivity(Intent(this, IssueListActivity::class.java))
        }
        findViewById<View>(R.id.btnMessages).setOnClickListener {
            /* TODO abrir MessagesActivity */
        }

        /* ---------- 3.   SAUDAR e REDIRECIONAR SE FOR GESTOR ---------- */
        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { snap ->
                val nome = snap.getString("name").orEmpty()
                val role = snap.getString("role").orEmpty()

                findViewById<TextView>(R.id.txtBemVindo)?.text = "Bem vindo/a\n$nome"

                if (role == "gestor") {
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish()
                }
            }
        }
    }

    /**
     * Lê a tag do include (formato "Texto|@drawable/icone") e aplica nos views
     * `txtLabel` e `imgIcon` que existem dentro de item_home_button.xml.
     */
    private fun configHomeButton(viewId: Int) {
        val root = findViewById<View>(viewId)
        if (root == null) {
            android.util.Log.e("CONFIG", "View $viewId não encontrada")
            return
        }

        android.util.Log.d("CONFIG", "Configurando botão com tag: ${root.tag}")

        val tagParts = root.tag?.toString()?.split("|")
        if (tagParts == null || tagParts.size < 2) {
            android.util.Log.e("CONFIG", "Tag inválida: ${root.tag}")
            return
        }

        val (label, drawablePath) = tagParts
        val drawableName = drawablePath.removePrefix("@drawable/")

        val txtLabel = root.findViewById<TextView>(R.id.txtLabel)
        val imgIcon = root.findViewById<ImageView>(R.id.imgIcon)

        android.util.Log.d("CONFIG", "Aplicando texto: $label e ícone: $drawableName")

        txtLabel?.text = label
        imgIcon?.setImageResource(
            resources.getIdentifier(drawableName, "drawable", packageName)
        )
    }
}
