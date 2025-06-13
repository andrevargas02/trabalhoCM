package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var txtEmpty: TextView
    private val notifications = mutableListOf<Notification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        txtEmpty = findViewById(R.id.txtEmpty)
        recyclerView = findViewById(R.id.recyclerNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NotificationAdapter(notifications) { notification ->
            deleteNotification(notification)
        }
        recyclerView.adapter = adapter

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loadNotifications()
    }

    private fun loadNotifications() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("notifications")
            .whereEqualTo("userID", userId)
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                notifications.clear()
                for (doc in result) {
                    doc.toObject(Notification::class.java)?.let {
                        notifications.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
                txtEmpty.visibility = if (notifications.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar notificações", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteNotification(notification: Notification) {
        db.collection("notifications").document(notification.notificationID)
            .delete()
            .addOnSuccessListener {
                notifications.remove(notification)
                adapter.notifyDataSetChanged()
                txtEmpty.visibility = if (notifications.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao eliminar notificação", Toast.LENGTH_SHORT).show()
            }
    }
}
