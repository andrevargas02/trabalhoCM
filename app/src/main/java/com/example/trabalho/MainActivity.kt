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
        setContentView(R.layout.activity_main)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { snapshot ->
                val role = snapshot.getString("role").orEmpty()
                if (role == "gestor") {
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish()
                } else if (role == "trabalhador") {
                    startActivity(Intent(this, WorkerHomeActivity::class.java))
                    finish()
                } else if (role == "client") {
                    startActivity(Intent(this, UserHomeActivity::class.java))
                    finish()
                } else {
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
