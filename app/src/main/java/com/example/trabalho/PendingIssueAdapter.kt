package com.example.trabalho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Issue

class PendingIssueAdapter(
    private val issues: List<Pair<String, Issue>>,
    private val onAccept: (String, Issue) -> Unit
) : RecyclerView.Adapter<PendingIssueAdapter.PendingIssueViewHolder>() {

    inner class PendingIssueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.txtDescription)
        val status: TextView = itemView.findViewById(R.id.txtStatus)
        val urgency: TextView = itemView.findViewById(R.id.txtUrgency)
        val location: TextView = itemView.findViewById(R.id.txtLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingIssueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_issue, parent, false)
        return PendingIssueViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingIssueViewHolder, position: Int) {
        val (id, issue) = issues[position]
        holder.description.text = issue.description
        holder.status.text = issue.status
        holder.urgency.text = issue.urgency
        holder.location.text = issue.location_id

        holder.itemView.setOnClickListener {
            onAccept(id, issue)
        }
    }

    override fun getItemCount(): Int = issues.size
}