package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db   by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // NOTE: O layout de MainActivity agora é apenas um container mínimo (ex.: um ProgressBar).
        setContentView(R.layout.activity_main)

        // 1) Verifica se o usuário está logado
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Se não estiver logado, vai direto para LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 2) Se estiver logado, verifica o role no Firestore
        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { snapshot ->
                val role = snapshot.getString("role").orEmpty()
                if (role == "gestor") {
                    // Redireciona para tela de gestor
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish()
                } else if (role == "trabalhador") {
                    // Redireciona para o menu do trabalhador
                    startActivity(Intent(this, WorkerHomeActivity::class.java))
                    finish()
                } else {
                    // Role inválido ou não definido → força logout e volta para login
                    Toast.makeText(
                        this,
                        "Role inválido. Efetue logout e tente novamente.",
                        Toast.LENGTH_LONG
                    ).show()
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Erro ao carregar dados do usuário: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }
}
