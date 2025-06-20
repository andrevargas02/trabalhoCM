package com.example.trabalho

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatListActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ChatListAdapter
    private val chats = mutableListOf<ChatItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        recycler = findViewById(R.id.recyclerViewChats)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = ChatListAdapter(chats) { chatId, otherUid ->
            val intent = Intent(this, ChatActivity::class.java)
                .putExtra("chatId", chatId)
                .putExtra("otherUid", otherUid)
            startActivity(intent)
        }
        recycler.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabNewChat)
            .setOnClickListener { showNewChatDialog() }

        listenForChats()
    }

    private fun listenForChats() {
        val uid = auth.currentUser!!.uid
        db.collection("chats")
            .whereArrayContains("participants", uid)
            .addSnapshotListener { snap, _ ->
                if (snap == null) return@addSnapshotListener
                chats.clear()
                for (doc in snap.documents) {
                    val parts = doc.get("participants") as List<String>
                    val otherUid = parts.first { it != uid }
                    val lastMsg = doc.getString("lastMessage").orEmpty()

                    val chatItem = ChatItem(doc.id, otherUid, lastMsg)
                    chats.add(chatItem)

                    db.collection("users").document(otherUid)
                        .get()
                        .addOnSuccessListener { userDoc ->
                            val name = userDoc.getString("name") ?: userDoc.getString("email") ?: otherUid
                            chatItem.otherUserName = name
                            adapter.notifyDataSetChanged()
                        }
                }
            }
    }

    private fun showNewChatDialog() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Novo chat (email do utilizador)")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val email = input.text.toString().trim()
                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { res ->
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
        val data = mapOf(
            "participants" to listOf(uid, otherUid),
            "lastMessage" to "",
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        db.collection("chats").add(data).addOnSuccessListener { doc ->
            startActivity(Intent(this, ChatActivity::class.java)
                .putExtra("chatId", doc.id)
                .putExtra("otherUid", otherUid))
        }
    }
}
