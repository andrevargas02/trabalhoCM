package com.example.trabalho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatListAdapter(
    private val items: List<Chat>,
    private val onClick: (chatId: String, otherUid: String) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.txtChatWith)
        val last = view.findViewById<TextView>(R.id.txtLastMessage)
    }

    override fun onCreateViewHolder(p: ViewGroup, i: Int): VH {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_chat, p, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val c = items[pos]
        h.name.text = c.otherName
        h.last.text = c.lastMessage
        h.itemView.setOnClickListener { onClick(c.id, /* otherUid unknown here */ "") }
    }
    override fun getItemCount() = items.size
}
