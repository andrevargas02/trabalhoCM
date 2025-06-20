package com.example.trabalho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Notification
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private val list: MutableList<Notification>,
    private val onDelete: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMessage: TextView = view.findViewById(R.id.txtMessage)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = list[position]
        holder.txtMessage.text = notification.message
        holder.txtDate.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(notification.timestamp))
        holder.btnDelete.setOnClickListener {
            onDelete(notification)
        }
    }
}

