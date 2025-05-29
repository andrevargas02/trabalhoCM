package com.example.trabalho

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalho.models.Issue
import com.google.firebase.firestore.FirebaseFirestore

class IssueListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IssueAdapter
    private val issues = mutableListOf<Issue>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_list)

        recyclerView = findViewById(R.id.recyclerViewIssues)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = IssueAdapter(issues)
        recyclerView.adapter = adapter

        db.collection("issues").get().addOnSuccessListener { result ->
            for (doc in result) {
                val issue = doc.toObject(Issue::class.java)
                issues.add(issue)
            }
            adapter.notifyDataSetChanged()
        }
    }
}
