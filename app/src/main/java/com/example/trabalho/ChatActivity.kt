package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Message
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: MessageAdapter
    private val msgs = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val otherUid = intent.getStringExtra("otherUid") ?: ""
        val titleView = findViewById<TextView>(R.id.txtChatTitle)
        db.collection("users").document(otherUid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: doc.getString("email") ?: otherUid
                titleView.text = "Chat com $name"
            }



        recycler = findViewById(R.id.recyclerViewMessages)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(msgs)
        recycler.adapter = adapter

        val chatId = intent.getStringExtra("chatId") ?: return

        findViewById<ImageButton>(R.id.btnSend).setOnClickListener {
            val input = findViewById<EditText>(R.id.inputMessage)
            val text = input.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            val m = mapOf(
                "senderId" to auth.currentUser!!.uid,
                "text" to text,
                "timestamp" to Timestamp.now()
            )
            db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(m)

            db.collection("chats")
                .document(chatId)
                .update("lastMessage", text)

            input.setText("")
        }

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snap, _ ->
                if (snap == null) return@addSnapshotListener
                msgs.clear()
                for (doc in snap.documents) {
                    doc.toObject(Message::class.java)?.let { msgs.add(it) }
                }
                adapter.notifyDataSetChanged()
                recycler.scrollToPosition(msgs.size - 1)
            }
    }
}
