package com.example.trabalho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Issue

class IssueAdapter(private val issues: List<Issue>) : RecyclerView.Adapter<IssueAdapter.IssueViewHolder>() {
    class IssueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.txtDescription)
        val status: TextView = itemView.findViewById(R.id.txtStatus)
        val urgency: TextView = itemView.findViewById(R.id.txtUrgency)
        val location: TextView = itemView.findViewById(R.id.txtLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_issue, parent, false)
        return IssueViewHolder(view)
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val issue = issues[position]
        holder.description.text = issue.description
        holder.status.text = issue.status
        holder.urgency.text = issue.urgency
        holder.location.text = issue.location_id
        // TODO: OnClickListener for details
    }

    override fun getItemCount() = issues.size
}
