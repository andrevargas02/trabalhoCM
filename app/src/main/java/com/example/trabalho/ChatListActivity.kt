package com.example.trabalho

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Chat(val id: String, val otherName: String, val lastMessage: String)

class ChatListActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var recycler: RecyclerView
    private val chats = mutableListOf<Chat>()
    private lateinit var adapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        recycler = findViewById(R.id.recyclerViewChats)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = ChatListAdapter(chats) { chatId, otherUid ->
            startActivity(Intent(this, ChatActivity::class.java)
                .putExtra("chatId", chatId)
                .putExtra("otherUid", otherUid))
        }
        recycler.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabNewChat)
            .setOnClickListener { showNewChatDialog() }

        listenChats()
    }

    private fun listenChats() {
        val uid = auth.currentUser!!.uid
        db.collection("chats")
            .whereArrayContains("participants", uid)
            .addSnapshotListener { snap, _ ->
                if (snap == null) return@addSnapshotListener
                chats.clear()
                for (doc in snap.documents) {
                    val data = doc.data!!
                    val parts = (data["participants"] as List<String>)
                    val otherUid = parts.first { it != uid }
                    val lastMsg = data["lastMessage"] as? String ?: ""
                    db.collection("users").document(otherUid).get()
                        .addOnSuccessListener { userSnap ->
                            chats.add(Chat(doc.id, userSnap.getString("name")!!, lastMsg))
                            adapter.notifyDataSetChanged()
                        }
                }
            }
    }

    private fun showNewChatDialog() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Novo chat (email)")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val email = input.text.toString()
                db.collection("users")
                    .whereEqualTo("email", email)
                    .get().addOnSuccessListener { res ->
                        if (res.isEmpty) return@addOnSuccessListener
                        val otherUid = res.documents[0].id
                        createOrOpenChat(otherUid)
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun createOrOpenChat(otherUid: String) {
        val uid = auth.currentUser!!.uid
        // simplificação: sempre cria novo chat
        val chat = mapOf(
            "participants" to listOf(uid, otherUid),
            "lastMessage" to "",
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        db.collection("chats").add(chat)
            .addOnSuccessListener { doc ->
                startActivity(Intent(this, ChatActivity::class.java)
                    .putExtra("chatId", doc.id)
                    .putExtra("otherUid", otherUid))
            }
    }
}
