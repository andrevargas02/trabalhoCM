package com.example.trabalho

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = findViewById<EditText>(R.id.inputName).text.toString()
            val phone = findViewById<EditText>(R.id.inputPhone).text.toString()
            val nif = findViewById<EditText>(R.id.inputNif).text.toString()
            val email = findViewById<EditText>(R.id.inputEmail).text.toString()
            val password = findViewById<EditText>(R.id.inputPassword).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.inputConfirmPassword).text.toString()

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser!!.uid
                        val user = hashMapOf(
                            "name" to name,
                            "phone" to phone,
                            "nif" to nif,
                            "email" to email,
                            "role" to "client"
                        )

                        db.collection("users").document(uid).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registo feito com sucesso!", Toast.LENGTH_SHORT).show()
                                // Aqui podes ir para a main/home:
                                // startActivity(Intent(this, MainActivity::class.java))
                                // finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao guardar dados", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
