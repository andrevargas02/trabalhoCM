package com.example.trabalho

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
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
    private val list = mutableListOf<Message>()
    private lateinit var chatId: String

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_chat)

        chatId = intent.getStringExtra("chatId")!!

        recycler = findViewById(R.id.recyclerViewMessages)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(list)
        recycler.adapter = adapter

        findViewById<ImageView>(R.id.btnSend).setOnClickListener {
            val input = findViewById<EditText>(R.id.inputMessage)
            val txt = input.text.toString().trim()
            if (txt.isEmpty()) return@setOnClickListener

            val msg = mapOf(
                "senderId" to auth.currentUser!!.uid,
                "text" to txt,
                "timestamp" to Timestamp.now()
            )
            db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(msg)
            db.collection("chats")
                .document(chatId)
                .update("lastMessage", txt)
            input.setText("")
        }

        // Ouvir em tempo real
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snap, _ ->
                if (snap == null) return@addSnapshotListener
                list.clear()
                for (doc in snap.documents) {
                    val m = doc.toObject(Message::class.java)
                    if (m != null) list.add(m)
                }
                adapter.notifyDataSetChanged()
                recycler.scrollToPosition(list.size - 1)
            }
    }
}
