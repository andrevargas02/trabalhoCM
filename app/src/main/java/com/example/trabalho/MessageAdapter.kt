package com.example.trabalho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Message
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(
    private val msgs: List<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun getItemViewType(position: Int): Int {
        return if (msgs[position].senderId == currentUserId) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            val v = inflater.inflate(R.layout.item_message_sent, parent, false)
            SentVH(v)
        } else {
            val v = inflater.inflate(R.layout.item_message_received, parent, false)
            ReceivedVH(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val m = msgs[position]
        if (holder is SentVH) {
            holder.txt.text = m.text
        } else if (holder is ReceivedVH) {
            holder.txt.text = m.text
        }
    }

    override fun getItemCount() = msgs.size

    class SentVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt: TextView = itemView.findViewById(R.id.txtSent)
    }

    class ReceivedVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt: TextView = itemView.findViewById(R.id.txtReceived)
    }
}
