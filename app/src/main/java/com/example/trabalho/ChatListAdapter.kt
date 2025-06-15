package com.example.trabalho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ChatItem(
    val chatId: String,
    val otherUid: String,
    val lastMessage: String,
    var otherUserName: String = "Utilizador"
)

class ChatListAdapter(
    private val items: List<ChatItem>,
    private val onClick: (chatId: String, otherUid: String) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.txtChatWith)
        val last = view.findViewById<TextView>(R.id.txtLastMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = items[position]
        holder.name.text = "Chat com ${c.otherUserName}"
        holder.last.text = c.lastMessage
        holder.itemView.setOnClickListener {
            onClick(c.chatId, c.otherUid)
        }
    }

    override fun getItemCount() = items.size
}
