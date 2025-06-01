package com.example.trabalho

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalho.models.User
import com.google.firebase.firestore.FirebaseFirestore

class ManageEmployeesActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var btnAddEmployee: Button
    private val userList = mutableListOf<User>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_employees)

        listView = findViewById(R.id.listEmployees)
        btnAddEmployee = findViewById(R.id.btnAddEmployee)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        loadUsers()

        listView.setOnItemClickListener { _, _, position, _ ->
            showRoleChangeDialog(userList[position])
        }

        btnAddEmployee.setOnClickListener {
            val intent = Intent(this, RegisterEmployeeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                val displayList = mutableListOf<String>()
                for (document in result) {
                    val user = document.toObject(User::class.java)
                    userList.add(user)
                    displayList.add("${user.name} - ${user.role}")
                }
                adapter.clear()
                adapter.addAll(displayList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("ManageEmployeesActivity", "Erro ao buscar utilizadores", exception)
                Toast.makeText(this, "Erro ao carregar utilizadores", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showRoleChangeDialog(user: User) {
        val roles = arrayOf("gestor", "trabalhador")
        AlertDialog.Builder(this)
            .setTitle("Trocar papel de ${user.name}")
            .setItems(roles) { _, which ->
                val newRole = roles[which]
                db.collection("users")
                    .whereEqualTo("email", user.email)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (doc in documents) {
                            db.collection("users").document(doc.id)
                                .update("role", newRole)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "${user.name} agora é $newRole", Toast.LENGTH_SHORT).show()
                                    loadUsers()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Erro ao atualizar papel", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao localizar utilizador", Toast.LENGTH_SHORT).show()
                    }
            }
            .show()
    }
}
