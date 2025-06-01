package com.example.trabalho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Issue

class ManageIssueAdapter(
    private val issues: List<Pair<String, Issue>>,
    private val onClick: (String, Issue) -> Unit
) : RecyclerView.Adapter<ManageIssueAdapter.IssueViewHolder>() {

    class IssueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDesc: TextView = view.findViewById(R.id.txtDescription)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
        val txtUrgency: TextView = view.findViewById(R.id.txtUrgency)
        val txtLocation: TextView = view.findViewById(R.id.txtLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_issue, parent, false)
        return IssueViewHolder(view)
    }

    override fun getItemCount(): Int = issues.size

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val (id, issue) = issues[position]
        holder.txtDesc.text = issue.description
        holder.txtStatus.text = "Estado: ${issue.status}"
        holder.txtUrgency.text = "Urgência: ${issue.urgency}"
        holder.txtLocation.text = "Local: ${issue.location_id}"
        holder.itemView.setOnClickListener {
            onClick(id, issue)
        }
    }
}
