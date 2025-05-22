package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val btnManageEmployees = findViewById<Button>(R.id.btnManageEmployees)
        val btnManageIssues = findViewById<Button>(R.id.btnManageIssues)

        btnManageEmployees.setOnClickListener {
            // TODO: Start ManageEmployeesActivity
        }

        btnManageIssues.setOnClickListener {
            val intent = Intent(this, IssueListActivity::class.java)
            startActivity(intent)
        }
    }
}
