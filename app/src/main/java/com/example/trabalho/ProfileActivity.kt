package com.example.trabalho

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val txtName = findViewById<TextView>(R.id.txtProfileName)
        val txtEmail = findViewById<TextView>(R.id.txtProfileEmail)
        val txtRole = findViewById<TextView>(R.id.txtProfileRole)

        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get().addOnSuccessListener { doc ->
            txtName.text = doc.getString("name") ?: ""
            txtEmail.text = doc.getString("email") ?: ""
            txtRole.text = doc.getString("role") ?: ""
        }
    }
}
