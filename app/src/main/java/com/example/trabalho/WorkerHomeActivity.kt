package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WorkerHomeActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db = FirebaseFirestore.getInstance()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        menuIcon = findViewById(R.id.imgMenu)

        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { snap ->
                val nome = snap.getString("name").orEmpty()
                findViewById<TextView>(R.id.txtBemVindo)
                    .text = "Bem vindo/a\n$nome"
            }
        }

        listOf(
            R.id.btnPendingIssues,
            R.id.btnActiveIssues,
            R.id.btnHistoryWorker,
            R.id.btnMessagesWorker
        ).forEach { configHomeButton(it) }

        findViewById<View>(R.id.btnPendingIssues).setOnClickListener {
            startActivity(Intent(this, PendingIssuesActivity::class.java))
        }
        findViewById<View>(R.id.btnActiveIssues).setOnClickListener {
            startActivity(Intent(this, ActiveIssuesActivity::class.java))
        }
        findViewById<View>(R.id.btnHistoryWorker).setOnClickListener {
            startActivity(Intent(this, WorkerIssueHistoryActivity::class.java))
        }
        findViewById<View>(R.id.btnMessagesWorker).setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun configHomeButton(viewId: Int) {
        val root = findViewById<View>(viewId) ?: return
        val parts = root.tag?.toString()?.split("|") ?: return
        if (parts.size < 2) return

        val (label, iconTag) = parts
        val iconName = iconTag.removePrefix("@drawable/")

        root.findViewById<TextView>(R.id.txtLabel)?.text = label
        root.findViewById<ImageView>(R.id.imgIcon)
            ?.setImageResource(resources.getIdentifier(iconName, "drawable", packageName))
    }
}
