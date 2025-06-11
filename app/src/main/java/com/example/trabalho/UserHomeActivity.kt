package com.example.trabalho

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.view.GravityCompat

class UserHomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        menuIcon = findViewById(R.id.imgMenu)

        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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

        listOf(
            R.id.btnCreateIssue,
            R.id.btnIssues,
            R.id.btnHistory,
            R.id.btnMessages
        ).forEach { configHomeButton(it) }

        findViewById<View>(R.id.btnCreateIssue).setOnClickListener {
            startActivity(Intent(this, CreateIssueActivity::class.java))
        }
        findViewById<View>(R.id.btnIssues).setOnClickListener {
            startActivity(Intent(this, ActiveIssuesActivity::class.java))
        }
        findViewById<View>(R.id.btnHistory).setOnClickListener {
            startActivity(Intent(this, WorkerIssueHistoryActivity::class.java))
        }
        findViewById<View>(R.id.btnMessages).setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }

        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { snap ->
                val nome = snap.getString("name").orEmpty()
                findViewById<TextView>(R.id.txtBemVindo).text = "Bem vindo/a\n$nome"
            }
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun configHomeButton(viewId: Int) {
        val root = findViewById<View>(viewId) ?: return
        val tagParts = root.tag?.toString()?.split("|") ?: return
        if (tagParts.size < 2) return

        val (label, drawablePath) = tagParts
        val drawableName = drawablePath.removePrefix("@drawable/")

        root.findViewById<TextView>(R.id.txtLabel)?.text = label
        root.findViewById<ImageView>(R.id.imgIcon)?.setImageResource(
            resources.getIdentifier(drawableName, "drawable", packageName)
        )
    }
}
